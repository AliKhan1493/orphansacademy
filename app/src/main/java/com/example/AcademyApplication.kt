package com.example

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import com.example.di.AppContainer

class AcademyApplication : Application(), Configuration.Provider {

    lateinit var container: AppContainer
        private set

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        try {
            container.startPeriodicSync()
        } catch (e: Exception) {
            Log.w("AcademyApplication", "WorkManager startup deferred: ${e.message}")
        }
    }
}
