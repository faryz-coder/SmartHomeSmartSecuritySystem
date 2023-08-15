package com.example.smarthomesmartsecuritysystem.utils.biometric

import android.util.Log
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.smarthomesmartsecuritysystem.R
import kotlin.reflect.KFunction0

// Since we are using the same methods in more than one Activity, better give them their own file.
object BiometricPromptUtils {
    private const val TAG = "BiometricPromptUtils"
    fun createBiometricPrompt(
        activity: FragmentActivity,
        processSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        processFailed: KFunction0<Unit>
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errCode, errString)
                Log.d(TAG, "errCode is $errCode and errString is: $errString")
                processFailed()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d(TAG, "User biometric rejected.")
                processFailed()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Log.d(TAG, "Authentication was successful")
                processSuccess(result)
            }
        }
        return BiometricPrompt(activity, executor, callback)
    }

    fun createPromptInfo(activity: FragmentActivity): BiometricPrompt.PromptInfo =
        BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(activity.getString(R.string.prompt_info_title))
            setSubtitle(activity.getString(R.string.prompt_info_subtitle))
            setDescription(activity.getString(R.string.prompt_info_description))
            setConfirmationRequired(false)
            setNegativeButtonText(activity.getString(R.string.prompt_info_use_app_password))
        }.build()
}