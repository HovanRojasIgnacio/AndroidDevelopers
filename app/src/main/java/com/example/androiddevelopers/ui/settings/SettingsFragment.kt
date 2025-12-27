package com.example.androiddevelopers.ui.settings

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.androiddevelopers.R
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SettingsFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        val switchDarkMode = view.findViewById<SwitchMaterial>(R.id.switch_dark_mode)
        val btnLogout = view.findViewById<Button>(R.id.btn_logout)

        val settingsManager = SettingsManager(requireContext())

        val currentMode = settingsManager.getNightMode()

        if (currentMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            switchDarkMode.isChecked = (currentNightMode == Configuration.UI_MODE_NIGHT_YES)
        } else {
            switchDarkMode.isChecked = (currentMode == AppCompatDelegate.MODE_NIGHT_YES)
        }

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val newMode = if (isChecked) {
                AppCompatDelegate.MODE_NIGHT_YES // Forzar Oscuro
            } else {
                AppCompatDelegate.MODE_NIGHT_NO  // Forzar Claro
            }

            settingsManager.setNightMode(newMode)

            AppCompatDelegate.setDefaultNightMode(newMode)
        }

        btnLogout.setOnClickListener {
            auth.signOut()

            Toast.makeText(context, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()

        }
    }
}