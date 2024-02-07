package com.casecode.pos.ui.signIn

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
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
import com.casecode.pos.utils.showSnackbar
import com.casecode.pos.viewmodel.AuthViewModel
import com.google.android.gms.common.SignInButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SignInActivity : AppCompatActivity()
{
   
   private lateinit var binding: ActivitySignInBinding
   private lateinit var launcher: ActivityResultLauncher<IntentSenderRequest>
   
   
   private val viewModel: AuthViewModel by viewModels()
   
   
   @Inject
   lateinit var auth: FirebaseAuth
   
   
   override fun onCreate(savedInstanceState: Bundle?)
   {
      super.onCreate(savedInstanceState)
      binding = ActivitySignInBinding.inflate(layoutInflater)
      setContentView(binding.root)
      
      
      launcher =
         registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK)
            {
               lifecycleScope.launch {
                   viewModel.signInWithIntent(
                     intent = result.data ?: return@launch)
               }
            }
         }
      
      binding.signInButton.setSize(SignInButton.SIZE_STANDARD)
      binding.signInButton.setOnClickListener {
         lifecycleScope.launch {
            val signInIntentSender = viewModel.signIn()
            launcher.launch(IntentSenderRequest.Builder(
               signInIntentSender ?: return@launch).build())
         }
         observerSignInResult()
         
      }
      binding.textEmployeeLogin.setOnClickListener{
         val employeeLogin = LoginDialogFragment()
         employeeLogin.show(supportFragmentManager, "Login")
      }
   }
   
   
   override fun onStart()
   {
      super.onStart()
      // Check if user is signed in (non-null) and update UI accordingly.
      updateUI()
   }
   
   private fun updateUI()
   {
      if (auth.currentUser != null)
      {
         viewModel.checkIfRegistrationAndBusinessCompleted()
         observerCheckRegistrationAndCompletedStep()
      }
   }
   
   private fun observerSignInResult()
   {
      viewModel.signInResult.observe(this) {
         when (it)
         {
            is FirebaseAuthResult.Failure ->
            {
               binding.root.showSnackbar("Sign in Failure", Snackbar.LENGTH_SHORT)
               
            }
            
            is FirebaseAuthResult.SignInFails ->
            {
               Timber.e("SignInFails")
               binding.root.showSnackbar("Sign in fails", Snackbar.LENGTH_SHORT)
               
               
            }
            
            is FirebaseAuthResult.SignInSuccess ->
            {
               Timber.e("SignInSuccess")
               updateUI()
               
               
            }
         }
      }
   }
   
   /**
    * Checking if a new user is creating a new user in the database and If completed steps business, otherwise: logging in directly
    */
   private fun observerCheckRegistrationAndCompletedStep()
   {
      viewModel.checkRegistration.observe(this) {
         when (val result = it)
         {
            
            is Resource.Success ->
            {
               if (result.data)
               {
                  moveToMainActivity()
               } else
               {
                  moveToStepperActivity()
               }
            }
            
            else ->
            {
               binding.root.showSnackbar(getString(R.string.all_error_save), Snackbar.LENGTH_SHORT)
            }
         }
      }
   }
   
   private fun moveToMainActivity()
   {
      val intent = Intent(this, MainActivity::class.java)
      intent.flags =
         Intent.FLAG_ACTIVITY_CLEAR_TOP // used to clean activity and al activities above it will be removed.
      startActivity(intent)
   }
   
   private fun moveToStepperActivity()
   {
      val intent = Intent(this, StepperActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
      startActivity(intent)
   }
   
   companion object
   {
      private const val TAG = "SignInActivity"
   }
}