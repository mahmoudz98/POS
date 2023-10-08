package com.casecode.pos

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree

@HiltAndroidApp
class POSApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree())

        FirebaseApp.initializeApp(applicationContext)
    }

}