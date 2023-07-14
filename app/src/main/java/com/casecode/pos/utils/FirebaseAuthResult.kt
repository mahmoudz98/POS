package com.casecode.pos.utils

import com.google.firebase.auth.FirebaseUser

sealed class FirebaseAuthResult {
    data class SignInSuccess(val user: FirebaseUser) : FirebaseAuthResult()
    data class SignInFails(val exception: Exception?) : FirebaseAuthResult()
    data class Failure(val exception: Exception?) : FirebaseAuthResult()
}