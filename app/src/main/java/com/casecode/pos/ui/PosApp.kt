package com.casecode.pos.ui

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import timber.log.Timber
import timber.log.Timber.DebugTree

class PosApp: Application() {



     override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree())
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        AppCompatDelegate.getApplicationLocales()
    }
    }