package com.example.smarthomesmartsecuritysystem.utils.biometric

import android.widget.Switch
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.DatabaseReference

interface BiometricInterface {
    fun showBiometricPromptForDecryption(activity: FragmentActivity, onSuccess: () -> Unit, onFailed: () -> Unit)

    fun verify(activity: FragmentActivity, myRef: DatabaseReference, switch: Switch, stat: Int)
}