package com.example.smarthomesmartsecuritysystem

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import com.example.smarthomesmartsecuritysystem.utils.biometric.BiometricPromptUtils
import com.example.smarthomesmartsecuritysystem.utils.biometric.BiometricUser
import com.example.smarthomesmartsecuritysystem.utils.biometric.CIPHERTEXT_WRAPPER
import com.example.smarthomesmartsecuritysystem.utils.biometric.CryptographyManager
import com.example.smarthomesmartsecuritysystem.utils.biometric.SHARED_PREFS_FILENAME
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase

@SuppressLint("UseSwitchCompatOrMaterialCode")
class SettingsFragment : Fragment() {
    private val db = Firebase.firestore
    private lateinit var userEmail: String
    private lateinit var cryptographyManager: CryptographyManager
    private lateinit var biometricSwitch: Switch
    private lateinit var sharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref =
            this.activity?.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
                ?: return
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        biometricSwitch = root.findViewById(R.id.r2_switch3)
        userEmail = requireActivity().intent.getStringExtra("email").toString()
        val myRef = db.collection("user").document(userEmail)
        myRef.get()
            .addOnSuccessListener {
                biometricSwitch.isChecked = it.getField<Boolean>("isBiometricActive") == true && sharedPref.getBoolean("isFingerprintActive", false)
                biometricSwitch.text = if (it.getField<Boolean>("isBiometricActive") == true && sharedPref.getBoolean("isFingerprintActive", false)) "ON" else "OFF"

                biometricSwitch.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        biometricSwitch.text = "ON"
                        showBiometricPromptForEncryption()
                    } else {
                        biometricSwitch.text = "OFF"
                        updateBiometric(isChecked)
                        updateInternalBiometric(false)
                    }
                }
            }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun updateBiometric(status: Boolean, biometricToken: String = "") {
        val data = hashMapOf(
            "isBiometricActive" to status,
            "biometricToken" to if (biometricToken.isEmpty()) "" else biometricToken
        )

        db.collection("user").document(userEmail).set(data, SetOptions.merge())
    }

    private fun showBiometricPromptForEncryption() {
        val canAuthenticate = BiometricManager.from(requireContext()).canAuthenticate()
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            val secretKeyName = getString(R.string.secret_key_name)
            cryptographyManager = CryptographyManager()
            val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
            val biometricPrompt =
                BiometricPromptUtils.createBiometricPrompt(
                    requireActivity(),
                    ::encryptAndStoreServerToken,
                    ::biometricFailed
                )
            val promptInfo = BiometricPromptUtils.createPromptInfo(requireActivity())
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    private fun encryptAndStoreServerToken(authResult: BiometricPrompt.AuthenticationResult) {
        BiometricUser.token = java.util.UUID.randomUUID().toString()
        authResult.cryptoObject?.cipher?.apply {
            BiometricUser.token?.let { token ->
                Log.d(TAG, "The token from server is $token")
                val encryptedServerTokenWrapper = cryptographyManager.encryptData(token, this)
                cryptographyManager.persistCiphertextWrapperToSharedPrefs(
                    encryptedServerTokenWrapper,
                    requireContext(),
                    SHARED_PREFS_FILENAME,
                    Context.MODE_PRIVATE,
                    CIPHERTEXT_WRAPPER
                )
                updateBiometric(true, token)
                biometricSwitch.text = "ON"
                updateInternalBiometric(true)
            }
        }
    }

    private fun biometricFailed() {
        biometricSwitch.isChecked = false
        biometricSwitch.text = "OFF"
    }

    private fun updateInternalBiometric(status: Boolean) {
        with(sharedPref.edit()) {
            putBoolean("isFingerprintActive", status)
            apply()
        }
        with(sharedPref.edit()) {
            putString("email", if(status) userEmail else  "")
            apply()
        }
    }
}