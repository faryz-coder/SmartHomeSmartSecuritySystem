package com.example.smarthomesmartsecuritysystem

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase

@SuppressLint("UseSwitchCompatOrMaterialCode")
class SettingsFragment : Fragment() {
    private val db = Firebase.firestore
    private lateinit var userEmail : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        val biometricSwitch : Switch = root.findViewById(R.id.r2_switch3)
        userEmail = requireActivity().intent.getStringExtra("email").toString()
        val myRef = db.collection("user").document(userEmail)
        myRef.get()
            .addOnSuccessListener {
                biometricSwitch.isChecked = it.getField<Boolean>("isBiometricActive") == true
            }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val biometricSwitch : Switch = view.findViewById(R.id.r2_switch3)

        biometricSwitch.setOnCheckedChangeListener {_, isChecked ->
            if (isChecked) {
                biometricSwitch.text = "ON"
                updateBiometric(isChecked)
            } else {
                biometricSwitch.text = "OFF"
                updateBiometric(isChecked)
            }
        }
    }

    private fun updateBiometric(status: Boolean) {
        val data = hashMapOf(
            "isBiometricActive" to status
        )
        db.collection("user").document(userEmail).set(data, SetOptions.merge())
    }
}