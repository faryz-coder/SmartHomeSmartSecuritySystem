package com.example.smarthomesmartsecuritysystem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.clubapplication.viewmodel.loginViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {
    private lateinit var viewModel: loginViewModel
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(requireActivity())[loginViewModel::class.java]

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<CardView>(R.id.btn_livingRoom).setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_LivingRoomFragment)
        }
        view.findViewById<CardView>(R.id.btn_room1).setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_RoomOneFragment)
        }
        view.findViewById<CardView>(R.id.btn_room2).setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_RoomTwoFragment)
        }
        view.findViewById<CardView>(R.id.btn_kitchen).setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_KitchenFragment)
        }
        view.findViewById<CardView>(R.id.btn_outsideLight).setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_outsideLightFragment)
        }

        lifecycleScope.launchWhenCreated {
            viewModel.name.collect { newName ->
                view.findViewById<TextView>(R.id.tv_welcome).text = "Welcome ${newName.capitalize()}"
            }
        }

    }
}