package com.casecode.pos.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // You can access the device token here
        // Use it to send notifications to this specific device
        // or store it for future use
        Log.d("FCM Token", token)
    }
}