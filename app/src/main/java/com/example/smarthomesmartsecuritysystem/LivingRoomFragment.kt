package com.example.smarthomesmartsecuritysystem

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.widget.SwitchCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class LivingRoomFragment : Fragment() {
    private val database = Firebase.database

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_living_room, container, false)
        val switch1 : Switch = root.findViewById(R.id.lr_switch1)
        val switch2 : Switch = root.findViewById(R.id.lr_switch2)
        val switch3 : Switch = root.findViewById(R.id.lr_switch3)

        val myRef = database.getReference("LivingRoom/switch1")
        val myRef2 = database.getReference("LivingRoom/switch2")
        val myRef3 = database.getReference("LivingRoom/switch3")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue<Int>()
                switch1.isChecked = value == 1
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
                switch2.isChecked = value == 1
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                d("bomoh", "Failed to Read Value")
            }
        })

        myRef3.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue<Int>()
                switch3.isChecked = value == 1
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                d("bomoh", "Failed to Read Value")
            }
        })


        return root
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val switch1 : Switch = view.findViewById(R.id.lr_switch1)
        val switch2 : Switch = view.findViewById(R.id.lr_switch2)
        val switch3 : Switch = view.findViewById(R.id.lr_switch3)

//        view.findViewById<Button>(R.id.button_second).setOnClickListener {
//            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
//        }

        switch1.setOnCheckedChangeListener { _, isChecked ->
            val myRef = database.getReference("LivingRoom/switch1")
            if (isChecked) {
                myRef.setValue(1)
            } else {
                myRef.setValue(0)
            }
        }

        switch2.setOnCheckedChangeListener { _, isChecked ->
            val myRef = database.getReference("LivingRoom/switch2")
            if (isChecked) {
                myRef.setValue(1)
            } else {
                myRef.setValue(0)
            }
        }

        switch3.setOnCheckedChangeListener { _, isChecked ->
            val myRef = database.getReference("LivingRoom/switch3")
            if (isChecked) {
                myRef.setValue(1)
            } else {
                myRef.setValue(0)
            }
        }

        view.findViewById<ImageView>(R.id.imageButton).setOnClickListener {
            it.findNavController().popBackStack()
        }
    }
}