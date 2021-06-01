package com.example.smarthomesmartsecuritysystem.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.smarthomesmartsecuritysystem.MainActivity
import com.example.smarthomesmartsecuritysystem.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase

/**
 * A placeholder fragment containing a simple view.
 */
class SignInFragment : Fragment() {

    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.signin_main, container, false)
        val buttonSignin: Button = root.findViewById(R.id.btn_login)
        val password: TextView = root.findViewById(R.id.et_password)
        val fname = root.findViewById<TextView>(R.id.et_name)
        val email = root.findViewById<TextView>(R.id.et_email)
        val lay: RelativeLayout = root.findViewById(R.id.layoutsignin)

        buttonSignin.setOnClickListener { v ->
            Log.d("bomoh", "signin pressed")
            if (valid()) {
                db.collection("user").document(email.text.toString())
                    .get()
                    .addOnSuccessListener {
                        Log.d("bomoh", "success")
                        val u_email = it.getField<String>("email").toString()
                        if (u_email == email.text.toString()) {
                            val pass = it.getField<String>("password").toString()
                            val bl = it.getField<String>("block").toString()
                            if (pass == password.text.toString()) {
                                Log.d("bomoh", "start")
                                if (bl != "yes"  || bl == "null") {
                                    val intent = Intent(root.context, MainActivity::class.java)
                                    intent.putExtra("email", email.text.toString())
                                    startActivity(intent)
                                    requireActivity().finish()
                                } else {
                                    Snackbar.make(v, "Account Blocked!", Snackbar.LENGTH_SHORT).show()
                                }
                            } else {
                                Snackbar.make(v, "Wrong Password or Email", Snackbar.LENGTH_SHORT).show()
                            }
                        }
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
        val email: TextView = requireView().findViewById(R.id.et_email)
        val password: TextView = requireView().findViewById(R.id.et_password)

        if (email.text.toString().isEmpty()) {
            email.error = "Required"
            valid = false
        } else {
            email.error = null
        }

        if (password.text.toString().isEmpty()) {
            password.error = "Required"
            valid = false
        } else {
            password.error = null
        }

        return valid
    }

    private fun closeKeyBoard(v : View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    companion object {

        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): SignInFragment {
            return SignInFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}