package com.example.smarthomesmartsecuritysystem.user

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.smarthomesmartsecuritysystem.MainActivity
import com.example.smarthomesmartsecuritysystem.R
import com.example.smarthomesmartsecuritysystem.utils.biometric.BiometricHandler
import com.example.smarthomesmartsecuritysystem.utils.biometric.CIPHERTEXT_WRAPPER
import com.example.smarthomesmartsecuritysystem.utils.biometric.CryptographyManager
import com.example.smarthomesmartsecuritysystem.utils.biometric.SHARED_PREFS_FILENAME
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase


/**
 * A placeholder fragment containing a simple view.
 */
class SignInFragment : Fragment() {

    val db = Firebase.firestore
    private lateinit var sharedPref: SharedPreferences
    private lateinit var biometricPrompt: BiometricPrompt
    private val cryptographyManager = CryptographyManager()
    private var limit = 0
    private val ciphertextWrapper
        get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            this.requireContext(),
            SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            CIPHERTEXT_WRAPPER
        )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref =
            this.activity?.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
                ?: return
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.signin_main, container, false)
        val buttonSignin: Button = root.findViewById(R.id.btn_login)
        val password: TextView = root.findViewById(R.id.et_password)
        val fname = root.findViewById<TextView>(R.id.et_name)
        val email = root.findViewById<TextView>(R.id.et_email)
        val lay: RelativeLayout = root.findViewById(R.id.layoutsignin)
        val biometricLoginBtn = root.findViewById<ImageButton>(R.id.biometricBtn)

        buttonSignin.setOnClickListener { v ->
            Log.d("bomoh", "signin pressed")
            if (valid()) {
                db.collection("user").document(email.text.toString())
                    .get()
                    .addOnSuccessListener {
                        Log.d("bomoh", "success")
                        val u_email = it.getField<String>("email").toString()
                        if (u_email == email.text.toString()) {
                            val pass = it.getField<String>("password").toString()
                            val bl = it.getField<String>("block").toString()
                            if (pass == password.text.toString()) {
                                Log.d("bomoh", "start")
                                if (bl != "yes" || bl == "null") {
                                    val intent = Intent(root.context, MainActivity::class.java)
                                    intent.putExtra("email", email.text.toString())
                                    startActivity(intent)
                                    requireActivity().finish()
                                } else {
                                    Snackbar.make(v, "Account Blocked!", Snackbar.LENGTH_SHORT)
                                        .show()
                                }
                            } else {
                                Snackbar.make(v, "Wrong Password or Email", Snackbar.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
            }
        }

        biometricLoginBtn.isVisible = sharedPref.getBoolean("isFingerprintActive", false)

        biometricLoginBtn.setOnClickListener {
            val savedEmail = sharedPref.getString("email", "")
            db.collection("user").document(savedEmail!!)
                .get()
                .addOnSuccessListener {
                    val token = it.getField<String>("biometricToken").toString()
                    // if token empty remove biometric
                    if (token.isNullOrEmpty()) {
                        with(sharedPref.edit()) {
                            putBoolean("isFingerprintActive", false)
                            apply()
                        }
                    } else {
                        // proceed with biometric authentication
                        BiometricHandler(requireContext()).showBiometricPromptForDecryption(requireActivity(),
                            { onBiometricSuccess() }, { biometricFailed() })
                    }
                }
        }

        lay.setOnClickListener {
            closeKeyBoard(it)
        }

        return root
    }

    private fun onBiometricSuccess() {
        val savedEmail = sharedPref.getString("email", "")

        val intent = Intent(this.requireContext(), MainActivity::class.java)
        intent.putExtra("email", savedEmail)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun biometricFailed() {
        Toast.makeText(requireContext(), "Login Failed", Toast.LENGTH_SHORT).show()
        limit += 1

        if (limit >= 3) {
            with(sharedPref.edit()) {
                putBoolean("isFingerprintActive", false)
                apply()
            }
            restartActivity()
        }
    }

    // Restart the current activity
    private fun restartActivity() {
        val intent = this.activity?.intent
        startActivity(intent)
        this.activity?.finish()
    }

    private fun valid(): Boolean {
        var valid = true
        val email: TextView = requireView().findViewById(R.id.et_email)
        val password: TextView = requireView().findViewById(R.id.et_password)

        if (email.text.toString().isEmpty()) {
            email.error = "Required"
            valid = false
        } else {
            email.error = null
        }

        if (password.text.toString().isEmpty()) {
            password.error = "Required"
            valid = false
        } else {
            password.error = null
        }

        return valid
    }

    private fun closeKeyBoard(v: View) {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    companion object {

        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): SignInFragment {
            return SignInFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}