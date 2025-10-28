package com.example.androiddevelopers

import android.app.Application
import androidx.room.Room

class DeveloperApp : Application() {

    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "triviaDatabase"
        )
            .createFromAsset("trivial.db")
            .build()
    }
}