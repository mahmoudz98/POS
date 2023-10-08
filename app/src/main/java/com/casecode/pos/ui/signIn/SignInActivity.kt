package com.casecode.pos.ui.signIn

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.casecode.pos.R
import com.casecode.pos.databinding.ActivitySignInBinding
import com.casecode.pos.ui.main.MainActivity
import com.casecode.pos.utils.FirebaseAuthResult
import com.casecode.pos.utils.FirebaseResult
import com.casecode.pos.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    private val viewModel: AuthViewModel by viewModels()

    // declare auth
    @Inject
    lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // config sign-in
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.signInButton.setSize(SignInButton.SIZE_STANDARD)
        binding.signInButton.setOnClickListener { signIn() }
    }

    // check user
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    // auth with google
    private fun firebaseAuthWithGoogle(idToken: String) {
        // Add additional functions for updating the UI or other tasks
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        lifecycleScope.launch {
            viewModel.signInWithCredential(credential).observe(this@SignInActivity) { result ->
                when (result) {
                    is FirebaseAuthResult.SignInSuccess -> {
                        // Sign in success, update UI with the signed-in user's information
                        Timber.d(TAG, "signInWithCredential:success")
                        updateUI(result.user)
                    }

                    is FirebaseAuthResult.SignInFails -> {
                        // If sign in fails, display a message to the user.
                        Timber.tag(TAG).w("signInWithCredential:ign in fails $result.exception")
                        updateUI(null)
                    }

                    is FirebaseAuthResult.Failure -> {
                        // If sign in fails, display a message to the user.
                        Timber.tag(TAG).w("signInWithCredential:failure $result.exception")
                        updateUI(null)
                    }
                }
            }
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startForResult.launch(signInIntent)
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle the result of the activity here
                val data: Intent? = result.data

                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Timber.tag(TAG).d("firebaseAuthWithGoogle: $account.id")
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Timber.tag(TAG).w("Google sign in failed $e")
                }
            }
        }

    private fun updateUI(currentUser: FirebaseUser?) {
        val email = currentUser?.email
        if (email != null) {
            viewModel.checkTheRegistration(email)
        }

        checkTheRegistration(currentUser)
    }

    /**
     * Checking if a new user is creating a new user in the database or logging in directly
     *
     * @param currentUser data for user
     */
    private fun checkTheRegistration(currentUser: FirebaseUser?) {
        viewModel.checkTheRegistration.observe(this) { result ->
            when (result) {
                is FirebaseResult.Success -> {
                    // Handle successful authentication
                    val signInMethods = result.data
                    // Check if the user exists
                    if (signInMethods.isNotEmpty()) {
                        /*
                        * 01. user exists
                        * 02. login
                        * */
                        Timber.tag(TAG).i("User exists")
                        moveToMainActivity(currentUser)
                    } else {
                        Timber.tag(TAG).i("User does not exist")
                        /*
                        * 01. user does not exist
                        * 02. create user on (Firebase)
                        * */

                    }
                }

                is FirebaseResult.Failure -> {
                    // Handle authentication failure
                    Timber.tag(TAG).e(result.exception)

                }
            }
        }
    }

    private fun moveToMainActivity(currentUser: FirebaseUser?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(getString(R.string.extra_uid), currentUser?.uid)
        intent.putExtra(getString(R.string.extra_display_name), currentUser?.displayName)
        intent.putExtra(getString(R.string.extra_email), currentUser?.email)
        intent.putExtra(getString(R.string.extra_phone_number), currentUser?.phoneNumber)
        intent.putExtra(getString(R.string.extra_photo_url), currentUser?.photoUrl.toString())
        startActivity(intent)
    }

    companion object {
        private const val TAG = "SignInActivity"
    }
}