package com.casecode.pos.ui.signIn

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.casecode.domain.utils.FirebaseAuthResult
import com.casecode.domain.utils.Resource
import com.casecode.pos.R
import com.casecode.pos.databinding.ActivitySignInBinding
import com.casecode.pos.ui.main.MainActivity
import com.casecode.pos.ui.stepper.StepperActivity
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
    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureGoogleSignIn()
        setupSignInButton()

        binding.textEmployeeLogin.setOnClickListener {
            showLoginDialog()
        }

    }

    private fun configureGoogleSignIn() {
        try {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)
        } catch (e: Exception) {
            Timber.tag(TAG).e("Error configuring Google Sign In: $e")
            // Handle the exception or log an error message
            // You might want to show a user-friendly message in case of a configuration error.
        }
    }

    private fun setupSignInButton() {
        binding.signInButton.setSize(SignInButton.SIZE_STANDARD)
        binding.signInButton.setOnClickListener { signIn() }
    }

    override fun onStart() {
        super.onStart()
        updateUI(auth.currentUser)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        lifecycleScope.launch {
            authViewModel.signInWithCredential(credential).collect { result ->
                handleAuthResult(result)
            }
        }
    }

    private fun handleAuthResult(result: FirebaseAuthResult) {
        when (result) {
            is FirebaseAuthResult.SignInSuccess -> updateUI(result.user)
            is FirebaseAuthResult.SignInFails, is FirebaseAuthResult.Failure -> updateUI(null)
            else -> {}
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startForResult.launch(signInIntent)
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                handleSignInResult(result.data)
            }
        }

    private fun handleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Timber.tag(TAG).w(getString(R.string.google_sign_in_failed, e))
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        currentUser?.let {
            authViewModel.checkTheRegistration(it.email.toString())
            checkTheRegistration(it)
        }
    }

    private fun checkTheRegistration(currentUser: FirebaseUser?) {
        lifecycleScope.launch {
            authViewModel.checkTheRegistration.collect { result ->
                when (result) {
                    is Resource.Success -> handleRegistrationSuccess(currentUser)
                    is Resource.Error -> Timber.tag(TAG).e(result.message)
                    is Resource.Empty, is Resource.Loading -> {}
                    else -> {}
                }
            }
        }
    }

    private fun handleRegistrationSuccess(currentUser: FirebaseUser?) {
        val created = currentUser?.metadata?.creationTimestamp ?: 0
        val current = System.currentTimeMillis() - 20000

        if (created < current) {
            Timber.tag(TAG).i(getString(R.string.user_exists))
            moveToMainActivity(currentUser)
        } else {
            Timber.tag(TAG).i(getString(R.string.user_does_not_exist))
            moveToStepperActivity(currentUser)
        }
    }

    private fun moveToMainActivity(currentUser: FirebaseUser?) {
        val intent = createIntent(MainActivity::class.java, currentUser)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun moveToStepperActivity(currentUser: FirebaseUser?) {
        startActivity(createIntent(StepperActivity::class.java, currentUser))
    }

    private fun createIntent(cls: Class<*>, currentUser: FirebaseUser?): Intent {
        return Intent(this, cls).apply {
            putExtra(getString(R.string.extra_uid), currentUser?.uid)
            putExtra(getString(R.string.extra_display_name), currentUser?.displayName)
            putExtra(getString(R.string.extra_email), currentUser?.email)
            putExtra(getString(R.string.extra_phone_number), currentUser?.phoneNumber)
            putExtra(getString(R.string.extra_photo_url), currentUser?.photoUrl.toString())
        }
    }

    private fun showLoginDialog() {
        val loginDialogFragment = LoginDialogFragment()
        loginDialogFragment.show(supportFragmentManager, "LoginDialogFragment")
    }

    companion object {
        private const val TAG = "SignInActivity"
    }

}