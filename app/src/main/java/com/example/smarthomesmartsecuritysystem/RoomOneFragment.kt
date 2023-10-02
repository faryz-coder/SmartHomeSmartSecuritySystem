package com.example.smarthomesmartsecuritysystem

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.clubapplication.viewmodel.loginViewModel
import com.example.smarthomesmartsecuritysystem.utils.biometric.BiometricHandler
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.lang.IllegalStateException

@SuppressLint("UseSwitchCompatOrMaterialCode")
class RoomOneFragment : Fragment() {
    val database = Firebase.database
    private lateinit var viewModel: loginViewModel
    private var done : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_room_one, container, false)
        viewModel = ViewModelProvider(requireActivity())[loginViewModel::class.java]
        done = false

        val switch1 : Switch = root.findViewById(R.id.r1_switch1)
        val switch2 : Switch = root.findViewById(R.id.r1_switch2)

        val myRef = database.getReference("RoomOne/switch1")
        val myRef2 = database.getReference("RoomOne/switch2")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue<Int>()
                d("bomoh", "value $value")
                if (switch2.isChecked != (value == 1)) {
                    done = false
                    switch1.isChecked = value == 1
                } else {
                    done = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                d("bomoh", "Failed to Read Value")
            }
        })

        myRef2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue<Int>()
                d("bomoh", "value $value")
                if (switch2.isChecked != (value == 1)) {
                    done = false
                    switch2.isChecked = value == 1
                } else {
                    done = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                d("bomoh", "Failed to Read Value")
            }
        })


        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val switch1 : Switch = view.findViewById(R.id.r1_switch1)
        val switch2 : Switch = view.findViewById(R.id.r1_switch2)
        val biometric = BiometricHandler(requireContext())

        switch1.setOnCheckedChangeListener { _, isChecked ->
            val myRef = database.getReference("RoomOne/switch1")
            d("bomoh", "RoomOneFragment onViewCreated ${viewModel.isBiometricActive} :: $done")
            if (isChecked) {
                if (viewModel.isBiometricActive && done) {
                    try {
                        biometric.verify(requireActivity(), myRef, switch1, 1)
                    } catch (e: IllegalStateException) {
                        d("bomoh", "IllegalStateException")
                    }
                } else {
                    myRef.setValue(1)
                }
            } else {
                myRef.setValue(0)
            }
            done = true
        }

        switch2.setOnCheckedChangeListener { _, isChecked ->
            val myRef = database.getReference("RoomOne/switch2")
            if (isChecked) {
                if (viewModel.isBiometricActive && done) {
                    try {
                        biometric.verify(requireActivity(), myRef, switch2, 1)
                    } catch (e: IllegalStateException) {
                        d("bomoh", "IllegalStateException")
                    }
                } else {
                    myRef.setValue(1)
                }
            } else {
                myRef.setValue(0)
            }
            done = true
        }
    }
}