package com.example.androiddevelopers

import android.app.Application
import androidx.room.Room
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.example.androiddevelopers.ui.events.Apis

class DeveloperApp : Application() , ImageLoaderFactory {

    /**
     * This creates the app-wide ImageLoader that Coil will use
     * for every .load() call.
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            // Tell Coil to use the OkHttpClient we already built in Apis.kt!
            .okHttpClient(Apis.client)
            .build()
    }

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