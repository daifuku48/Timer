package com.example.timer

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.fragment.app.Fragment
import androidx.preference.*
import com.example.timer.databinding.ActivityMainBinding
import java.lang.Exception

class SettingsFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {
    lateinit var binding: ActivityMainBinding
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.timer_preferences)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val sharedPreferences = preferenceScreen.sharedPreferences
        val preferenceScreen = preferenceScreen
        val count = preferenceScreen.preferenceCount //all is down for changing name in settings with Melody
        for (i in 0 until count)
        {
            val preference = preferenceScreen.getPreference(i)
            if (preference !is CheckBoxPreference)
            {
                val value : String? = sharedPreferences?.getString(preference.key, "")
                setPreferenceLabel(preference,value)
            }
        }

        val preference : Preference = findPreference<Preference>("default_interval")!!
        preference.onPreferenceChangeListener = this
    }

    private fun setPreferenceLabel(preference: Preference, value: String?)
    {
        if (preference is ListPreference)
        {
            val listPreference : ListPreference = preference
            val index = listPreference.findIndexOfValue(value)
            if (index >= 0) {
                listPreference.summary = listPreference.entries[index]
            }
        } else if (preference is EditTextPreference)
        {
            preference.summary = value
        }

    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        val preference = preferenceManager.findPreference<Preference>(p1 as String)
        if (preference is ListPreference)
        {
            val value = p0?.getString(p1, "bell")
            setPreferenceLabel(preference, value)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        if (preference.key.equals("default_interval")){
            val defaultIntervalString = newValue as String
            try {
                val defaultFormat = Integer.parseInt(defaultIntervalString)
            } catch(e: Exception){
                makeText(context, "Write normal number bidlo", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }
}