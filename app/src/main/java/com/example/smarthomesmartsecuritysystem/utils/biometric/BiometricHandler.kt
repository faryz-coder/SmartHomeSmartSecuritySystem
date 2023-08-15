package com.example.smarthomesmartsecuritysystem.utils.biometric

import android.content.Context
import android.util.Log
import android.widget.Switch
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.example.smarthomesmartsecuritysystem.R
import com.google.firebase.database.DatabaseReference

class BiometricHandler(context: Context) :  BiometricInterface {
    private lateinit var cryptographyManager: CryptographyManager
    private val context = context
    private val ciphertextWrapper : CiphertextWrapper?
        get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            context,
            SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            CIPHERTEXT_WRAPPER
        )

    override fun showBiometricPromptForDecryption(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onFailed: () -> Unit
    ) {
        val canAuthenticate = BiometricManager.from(context).canAuthenticate()
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            val secretKeyName = context.getString(R.string.secret_key_name)
            cryptographyManager = CryptographyManager()
            val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
            val biometricPrompt =
                BiometricPromptUtils.createBiometricPrompt(
                    activity,
                    ::decryptServerTokenFromStorage,
                    onSuccess,
                    onFailed
                )
            val promptInfo = BiometricPromptUtils.createPromptInfo(activity)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    override fun verify(
        activity: FragmentActivity,
        myRef: DatabaseReference,
        switch: Switch,
        stat: Int
    ) {
        showBiometricPromptForDecryption(
            activity,
            { myRef.setValue(stat) },
            { switch.isChecked = false })
    }

    private fun decryptServerTokenFromStorage(authResult: BiometricPrompt.AuthenticationResult, onSuccess: () -> Unit) {

        ciphertextWrapper?.let { textWrapper ->
            authResult.cryptoObject?.cipher?.let {
                val plaintext =
                    cryptographyManager.decryptData(textWrapper.ciphertext, it)
                BiometricUser.token = plaintext
                // Now that you have the token, you can query server for everything else
                // the only reason we call this fakeToken is because we didn't really get it from
                // the server. In your case, you will have gotten it from the server the first time
                // and therefore, it's a real token.

                Log.d("bomoh", "BiometricLogin:: Success")
                onSuccess.invoke()
            }
        }
    }
}