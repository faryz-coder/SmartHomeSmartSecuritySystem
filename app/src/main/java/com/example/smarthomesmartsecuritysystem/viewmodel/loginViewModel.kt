package com.example.clubapplication.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class loginViewModel : ViewModel() {

    var id : String? = null
    private val _name = MutableStateFlow<String>("")

    var isBiometricActive : Boolean = false
    private lateinit var sharedPref: SharedPreferences
    private val _sharedPreferenceData = MutableLiveData<Boolean>()

    val name: StateFlow<String>
        get() = _name

    fun updateName(newValue: String) {
        _name.value = newValue
    }
    val sharedPreferenceData: LiveData<Boolean> get() = _sharedPreferenceData
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if (key == "isFingerprintActive") { // Replace "your_key_here" with the actual key you're observing
            val updatedValue = sharedPreferences.getBoolean(key, false)
            _sharedPreferenceData.postValue(updatedValue)
        }
    }

    fun startObserving(sharedPreferences: SharedPreferences) {
        _sharedPreferenceData.value = sharedPreferences.getBoolean("isFingerprintActive", false)
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        sharedPref = sharedPreferences
    }

    override fun onCleared() {
        super.onCleared()
        // Don't forget to unregister the listener when the ViewModel is cleared
        sharedPref.unregisterOnSharedPreferenceChangeListener(listener)
    }
}