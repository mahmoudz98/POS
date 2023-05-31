package com.casecode.pos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.casecode.pos.databinding.ActivityMainBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // initialize auth
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // get user profile
        getUserProfile()

        binding.textView.setOnClickListener { signOut() }
    }

    private fun checkCurrentUser() {
        // [START check_current_user]
        val user = auth.currentUser
        if (user != null) {
            // User is signed in
        } else {
            // No user is signed in
        }
        // [END check_current_user]
    }

    private fun getUserProfile() {
        // get user profile
        val user = auth.currentUser
        user?.let {
            // Name, email address, and profile photo Url
            val name = it.displayName
            val email = it.email
            val photoUrl = it.photoUrl

            // Check if user's email is verified
            val emailVerified = it.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            val uid = it.uid
        }
    }

    private fun getProviderData() {
        val user = auth.currentUser
        user?.let {
            for (profile in it.providerData) {
                // Id of the provider (ex: google.com)
                val providerId = profile.providerId

                // UID specific to the provider
                val uid = profile.uid

                // Name, email address, and profile photo Url
                val name = profile.displayName
                val email = profile.email
                val photoUrl = profile.photoUrl
            }
        }
    }

//    private fun updateProfile() {
//        val user = auth.currentUser
//
//        val profileUpdates = userProfileChangeRequest {
//            displayName = "Jane Q. User"
//            photoUri = Uri.parse("https://example.com/jane-q-user/profile.jpg")
//        }
//
//        user!!.updateProfile(profileUpdates)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Log.d(TAG, "User profile updated.")
//                }
//            }
//    }

    private fun updateEmail() {
        val user = auth.currentUser

        user!!.updateEmail("user@example.com").addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User email address updated.")
                }
            }
    }

    private fun updatePassword() {
        val user = auth.currentUser
        val newPassword = "SOME-SECURE-PASSWORD"

        user!!.updatePassword(newPassword).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User password updated.")
                }
            }
    }

    private fun sendEmailVerification() {
        val user = auth.currentUser

        user!!.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                }
            }
    }

    private fun sendEmailVerificationWithContinueUrl() {
        val auth = auth
        val user = auth.currentUser!!

        val url = "http://www.example.com/verify?uid=" + user.uid
        val actionCodeSettings =
            ActionCodeSettings.newBuilder().setUrl(url).setIOSBundleId("com.example.ios")
                // The default for this is populated with the current android package name.
                .setAndroidPackageName("com.example.android", false, null).build()

        user.sendEmailVerification(actionCodeSettings).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                }
            }

        auth.setLanguageCode("fr")
        // To apply the default app language instead of explicitly setting it.
        // auth.useAppLanguage()
    }

    private fun sendPasswordReset() {
        val emailAddress = "user@example.com"

        auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                }
            }
    }

    private fun deleteUser() {
        val user = auth.currentUser!!

        user.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User account deleted.")
                }
            }
    }

    private fun testPhoneVerify() {
        val phoneNum = "+16505554567"
        val testVerificationCode = "123456"

        // Whenever verification is triggered with the whitelisted number,
        // provided it is not set for auto-retrieval, onCodeSent will be triggered.
        val options = PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phoneNum)
            .setTimeout(30L, TimeUnit.SECONDS).setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onCodeSent(
                    verificationId: String,
                    forceResendingToken: PhoneAuthProvider.ForceResendingToken,
                ) {
                    // Save the verification id somewhere
                    // ...

                    // The corresponding whitelisted code above should be used to complete sign-in.
                    this@MainActivity.enableUserManuallyInputCode()
                }

                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    // Sign in with the credential
                    // ...
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // ...
                }
            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun enableUserManuallyInputCode() {
        // No-op
    }

    private fun testPhoneAutoRetrieve() {
        // The test phone number and code should be whitelisted in the console.
        val phoneNumber = "+16505554567"
        val smsCode = "123456"

        val firebaseAuth = auth
        val firebaseAuthSettings = firebaseAuth.firebaseAuthSettings

        // Configure faking the auto-retrieval with the whitelisted numbers.
        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneNumber, smsCode)

        val options = PhoneAuthOptions.newBuilder(firebaseAuth).setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS).setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Instant verification is applied and a credential is directly returned.
                    // ...
                }

                // [START_EXCLUDE]
                override fun onVerificationFailed(e: FirebaseException) {
                }
                // [END_EXCLUDE]
            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signOut() {
        if (auth.currentUser != null)
            // user logged in already, do your work here for logged in user
            auth.signOut()
        else {
            // user is not logged in, let user login
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent);
            finish()
        }
    }

    companion object {
        private val TAG = MainActivity::class.simpleName
    }
}