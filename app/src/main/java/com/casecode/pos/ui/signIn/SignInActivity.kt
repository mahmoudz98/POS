package com.casecode.pos.ui.signIn

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.casecode.domain.utils.FirebaseAuthResult
import com.casecode.domain.utils.Resource
import com.casecode.pos.R
import com.casecode.pos.databinding.ActivitySignInBinding
import com.casecode.pos.ui.main.MainActivity
import com.casecode.pos.ui.stepper.StepperActivity
import com.casecode.pos.utils.showSnackbar
import com.casecode.pos.viewmodel.AuthViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.UnsupportedApiCallException
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {
    private var _binding: ActivitySignInBinding? = null
    val binding get() = _binding!!
    private lateinit var launcher: ActivityResultLauncher<IntentSenderRequest>
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLauncherActivity()
        viewModel.setNetworkMonitor()
        binding.signInButton.setSize(SignInButton.SIZE_STANDARD)
        clickedButtons()
        observerSignInIntentSender()
    }

    private fun setupLauncherActivity() {
        launcher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->

                if (result.resultCode == RESULT_OK) {
                    Timber.e("result is ok =  $RESULT_OK")
                    // Issue: when this launcher is ok is ok every time in
                    viewModel.signInWithIntent(result.data ?: return@registerForActivityResult)
                    observerSignInResult()

                }
            }
    }

    private fun clickedButtons() {
        binding.signInButton.setOnClickListener {
            if (viewModel.isOnline.value == true) {
                checkGooglePlayServices()
            } else {
                binding.root.showSnackbar(getString(R.string.network_error), Snackbar.LENGTH_SHORT)
            }
        }
        binding.textEmployeeLogin.setOnClickListener {
            val employeeLogin = LoginDialogFragment()
            employeeLogin.show(supportFragmentManager, "Login")
        }
    }

    private fun checkGooglePlayServices() {
        if (isGooglePlayServicesAvailable()) {
            // Google Play services are available, proceed with sign-in
            try {
                viewModel.signIn()
            } catch (e: UnsupportedApiCallException) {
                showAlternativeSignInDialog()

            }
        } else {
            // Google Play services are not available
            showAlternativeSignInDialog()
        }
    }


    private fun isGooglePlayServicesAvailable(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        return resultCode == ConnectionResult.SUCCESS
    }

    private fun showAlternativeSignInDialog() {
        // Display a dialog or message informing the user about the lack of Google Play services
        // and provide an option to download them from the Play Store.

        AlertDialog.Builder(this)
            .setTitle(R.string.google_play_services_required)
            .setMessage(R.string.google_play_services_message)
            .setPositiveButton(R.string.download) { _, _ ->
                openGooglePlayStore()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                // Handle the cancellation or provide an alternative sign-in method
                dialog.dismiss()
            }
            .show()
    }

    private fun openGooglePlayStore() {
        val playStoreIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms")
        )
        try {
            startActivity(playStoreIntent)
        } catch (e: ActivityNotFoundException) {
            // Handle the case where the Play Store app is not installed
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms")
            )
            startActivity(webIntent)
        }
    }

    private fun observerSignInIntentSender() {

        viewModel.signInIntentSender.observe(this) {
            when (it) {
                is Resource.Loading -> {// TODO: Support loading when intent is loading.
                }

                is Resource.Error -> {
                    val messageRes = it.message as? Int ?: R.string.all_error_save
                    if(messageRes == com.casecode.pos.data.R.string.unsupported_api_call){
                        showAlternativeSignInDialog()
                    }
                    binding.root.showSnackbar(getString(messageRes), Snackbar.LENGTH_SHORT)
                }

                is Resource.Empty -> {
                    binding.root.showSnackbar(
                        getString(R.string.all_error_save),
                        Snackbar.LENGTH_SHORT
                    )
                }

                is Resource.Success -> {
                    launcher.launch(
                        IntentSenderRequest.Builder(it.data).build()
                    )
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        updateUI()
    }

    private fun updateUI() {
        Timber.e("Update UI")
        lifecycleScope.launch {
            viewModel.currentUserUID.collect { uid ->
                Timber.e("update ui, UID = $uid")
                if (uid.isNotBlank()) {
                    viewModel.checkIfRegistrationAndBusinessCompleted()
                    observerCheckRegistrationAndCompletedStep()
                }
            }
        }
    }

    private fun observerSignInResult() {
        viewModel.signInResult.observe(this) {
            when (it) {
                is FirebaseAuthResult.Failure -> {
                    binding.root.showSnackbar(
                        getString(R.string.all_error_save),
                        Snackbar.LENGTH_SHORT
                    )

                }

                is FirebaseAuthResult.SignInFails -> {
                    Timber.e("SignInFails")
                    binding.root.showSnackbar(
                        getString(R.string.all_error_save),
                        Snackbar.LENGTH_SHORT
                    )
                }

                is FirebaseAuthResult.SignInSuccess -> {
                    Timber.i("SignInSuccess")
                    updateUI()
                    viewModel.clearSignInResult()

                }

                null -> {
                }
            }
        }
    }

    /**
     * Checking if a new user is creating a new user in the database and If completed steps business, otherwise: logging in directly
     */
    private fun observerCheckRegistrationAndCompletedStep() {
        viewModel.checkRegistration.observe(this) {
            when (val result = it) {

                is Resource.Success -> {
                    if (result.data) {
                        moveToMainActivity()
                    } else {
                        moveToStepperActivity()
                    }
                    viewModel.clearCheckRegistration()
                }

                is Resource.Empty -> {

                    binding.root.showSnackbar(
                        getString(com.casecode.pos.domain.R.string.uid_empty), Snackbar.LENGTH_SHORT
                    )
                }

                is Resource.Loading -> {
                }

                else -> {
                    binding.root.showSnackbar(
                        getString(R.string.all_error_save),
                        Snackbar.LENGTH_SHORT
                    )
                }
            }
        }
    }

    private fun moveToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }

    private fun moveToStepperActivity() {
        val intent = Intent(this, StepperActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onStop() {
        super.onStop()
        Timber.e("onStop")

    }

    override fun onDestroy() {
        Timber.e("onDestroy")
        super.onDestroy()
        removeObservers()


    }

    private fun removeObservers() {
        for (field in viewModel.javaClass.declaredFields) {

            field.isAccessible = true
            val fieldValue = field.get(viewModel)
            if (fieldValue is LiveData<*>) {
                fieldValue.removeObservers(this)
            }
        }

    }

}