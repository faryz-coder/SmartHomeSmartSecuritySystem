package com.example.smarthomesmartsecuritysystem

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import androidx.navigation.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

@SuppressLint("UseSwitchCompatOrMaterialCode")
class OutsideLightFragment : Fragment() {
    private val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_outside_light, container, false)
        val switch1 : Switch = root.findViewById(R.id.ol_switch1)
        val switch2 : Switch = root.findViewById(R.id.ol_switch2)

        val myRef = database.getReference("OutsideLight/switch1")
        val myRef2 = database.getReference("OutsideLight/switch2")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue<Int>()
                switch1.isChecked = value == 1
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.d("bomoh", "Failed to Read Value")
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
                Log.d("bomoh", "Failed to Read Value")
            }
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val switch1 : Switch = view.findViewById(R.id.ol_switch1)
        val switch2 : Switch = view.findViewById(R.id.ol_switch2)

        switch1.setOnCheckedChangeListener { _, isChecked ->
            val myRef = database.getReference("OutsideLight/switch1")
            if (isChecked) {
                myRef.setValue(1)
            } else {
                myRef.setValue(0)
            }
        }

        switch2.setOnCheckedChangeListener { _, isChecked ->
            val myRef = database.getReference("OutsideLight/switch2")
            if (isChecked) {
                myRef.setValue(1)
            } else {
                myRef.setValue(0)
            }
        }

        view.findViewById<ImageView>(R.id.imageButton5).setOnClickListener {
            it.findNavController().popBackStack()
        }
    }
}