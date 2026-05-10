package com.example.fincurr

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.fincurr.di.AppContainer

class FincurrApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        val mode = if (container.prefs.darkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}
