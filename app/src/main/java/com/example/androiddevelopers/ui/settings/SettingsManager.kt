package com.example.androiddevelopers.ui.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class SettingsManager(context: Context) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    // Guardamos un Entero (Int), no un Booleano
    // Por defecto será: MODE_NIGHT_FOLLOW_SYSTEM
    fun getNightMode(): Int {
        return prefs.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    fun setNightMode(mode: Int) {
        prefs.edit().putInt("night_mode", mode).apply()
    }


}