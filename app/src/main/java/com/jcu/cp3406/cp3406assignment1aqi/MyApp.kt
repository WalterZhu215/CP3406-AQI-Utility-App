package com.jcu.cp3406.cp3406assignment1aqi

import android.app.Application
import com.jcu.cp3406.cp3406assignment1aqi.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Custom Application class
 * Initializes Koin dependency injection on app startup
 */
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin DI framework
        startKoin {
            androidContext(this@MyApp)
            modules(appModule)
        }
    }
}