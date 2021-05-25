package com.example.smarthomesmartsecuritysystem.user

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.viewpager.widget.ViewPager
import com.example.smarthomesmartsecuritysystem.MainActivity2
import com.example.smarthomesmartsecuritysystem.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SignUpFragment : Fragment() {
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.signup_main, container, false)
        val buttonRegister: Button = root.findViewById(R.id.btn_register)
        val password: TextView = root.findViewById(R.id.et_password)
        val fname = root.findViewById<TextView>(R.id.et_name)
        val email = root.findViewById<TextView>(R.id.et_email)
        val lay: LinearLayout = root.findViewById(R.id.layoutsignup)

        buttonRegister.setOnClickListener {
//            root.findNavController().popBackStack()
            val tabs: TabLayout = requireActivity().findViewById(R.id.tabs)
            val viewPager: ViewPager = requireActivity().findViewById(R.id.view_pager)
            tabs.setupWithViewPager(viewPager)


            val v = it
            if (!valid()) {
                return@setOnClickListener
            } else {
                // Submit register profile to db
                val data = hashMapOf(
                    "password" to password.text.toString(),
                    "full name" to fname.text.toString(),
                    "email" to email.text.toString()
                )

                db.collection("user").document(email.text.toString())
                    .set(data)
                    .addOnSuccessListener {
                        Log.d("bomoh", "Register Complete")
                        Toast.makeText(context, "Register Complete", Toast.LENGTH_SHORT)
                        tabs.setScrollPosition(0,0f, true)
                        viewPager.setCurrentItem(0);
                    }
                    .addOnFailureListener { e ->
                        Snackbar.make(v, "Register Failed $e", Snackbar.LENGTH_SHORT)
                    }
            }
        }
        lay.setOnClickListener {
            closeKeyBoard(it)
        }
        return root
    }

    private fun valid(): Boolean {
        var valid = true
        val password = requireView().findViewById<TextView>(R.id.et_password)
        val confirmPassword = requireView().findViewById<TextView>(R.id.et_repassword)
        val fname = requireView().findViewById<TextView>(R.id.et_name)
        val email = requireView().findViewById<TextView>(R.id.et_email)

        if (password.text.toString().isEmpty()) {
            password.error = "enter password"
            valid = false
        } else {
            password.error = null
        }

        if (confirmPassword.text.toString().isEmpty()) {
            confirmPassword.error = "re-enter password"
            valid = false
        } else {
            confirmPassword.error = null
            // check if the password == confirm password or not
            if (password.text.toString() != confirmPassword.text.toString()) {
                confirmPassword.error = "enter the same password"
                valid = false
            } else {
                confirmPassword.error = null
            }
        }

        if (fname.text.toString().isEmpty()) {
            fname.error = "enter full name"
            valid = false
        } else {
            fname.error = null
        }

        if (email.text.toString().isEmpty()) {
            email.error = "enter email"
            valid = false
        } else {
            email.error = null
        }


        return valid

    }

    private fun closeKeyBoard(v : View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    companion object {

        private const val ARG_PARAM1 = "section"

        @JvmStatic
        fun newInstance(param1: String) =
            SignUpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}