package com.example.smarthomesmartsecuritysystem

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.clubapplication.viewmodel.loginViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    var username : String? = null
    val db = Firebase.firestore
    private lateinit var navController: NavController
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        sharedPref =
            this.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
                ?: return

        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        username = intent.getStringExtra("email").toString()
        var viewModel = ViewModelProvider(this)[loginViewModel::class.java]
//        viewModel.id = "admin"
//        username = "admin"
        viewModel.id = username
        viewModel.startObserving(sharedPref)

        viewModel.sharedPreferenceData.observe(this) { newValue ->
            viewModel.isBiometricActive = newValue
            d("bomoh", "isBiometricActive :: $newValue")
        }

        // Check if acc blocked
        val docRef = db.collection("user").document(username.toString())

        docRef.get().addOnSuccessListener { document ->
            val block = document.getField<String>("block").toString()
            if (!block.isNullOrEmpty()) {
                if (block == "yes") {
                    d("bomoh", "block account")
                    val intent = Intent(this, MainActivity2::class.java)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                val fname = document.getField<String>("full name").toString()
                Snackbar.make(this.window.decorView.rootView, "Welcome, $fname", Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                Log.d("MainActivity", "settings clicked!")
                navController.navigate(R.id.settingsFragment)
                return true
            }
            R.id.action_logout -> {
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
                this.finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}