package com.example.smarthomesmartsecuritysystem

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_livingRoom).setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_LivingRoomFragment)
        }
        view.findViewById<Button>(R.id.btn_room1).setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_RoomOneFragment)
        }
        view.findViewById<Button>(R.id.btn_room2).setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_RoomTwoFragment)
        }
        view.findViewById<Button>(R.id.btn_kitchen).setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_KitchenFragment)
        }
        view.findViewById<Button>(R.id.btn_outsideLight).setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_outsideLightFragment)
        }

        view.findViewById<Button>(R.id.btn_logout).setOnClickListener {
            val intent = Intent(context, MainActivity2::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}