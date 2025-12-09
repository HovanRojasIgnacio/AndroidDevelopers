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

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchDarkMode = view.findViewById<SwitchMaterial>(R.id.switch_dark_mode)
        val switchNotifications = view.findViewById<SwitchMaterial>(R.id.switch_notifications)
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


        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(context, "Notificaciones activadas", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Notificaciones desactivadas", Toast.LENGTH_SHORT).show()
            }
        }


        btnLogout.setOnClickListener {
            Toast.makeText(context, "Cerrando sesión...", Toast.LENGTH_SHORT).show()
        }
    }
}