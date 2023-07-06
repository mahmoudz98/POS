package com.casecode.pos.ui

import android.app.Application
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

private const val TAG = "PosApp"
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