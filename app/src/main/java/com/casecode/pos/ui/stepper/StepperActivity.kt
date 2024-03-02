package com.casecode.pos.ui.stepper

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.aceinteract.android.stepper.StepperNavListener
import com.casecode.pos.R
import com.casecode.pos.databinding.ActivityStepperBinding
import com.casecode.pos.ui.main.MainActivity
import com.casecode.pos.ui.signIn.SignInActivity
import com.casecode.pos.utils.EventObserver
import com.casecode.pos.utils.setupSnackbar
import com.casecode.pos.utils.showSnackbar
import com.casecode.pos.viewmodel.StepperBusinessViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class StepperActivity : AppCompatActivity(), StepperNavListener {
    private var _binding: ActivityStepperBinding? = null
    private val binding: ActivityStepperBinding get() = _binding!!

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val businessViewModel: StepperBusinessViewModel by viewModels()
    internal val viewModel: StepperBusinessViewModel get() = businessViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityStepperBinding.inflate(layoutInflater)
        setContentView(_binding?.root)
        onSupportBackPressed()
        setupStepper()
        setupSnackbar()
        observerUId()
        observerSteps()
        observerNetwork()
    }

    private fun setupStepper() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.frame_stepper) as NavHostFragment

        val navController = navHostFragment.navController
        binding.stepper.setupWithNavController(navController)
    }

    private fun setupSnackbar() {
        binding.root.setupSnackbar(
            this,
            businessViewModel.userMessage,
            Snackbar.LENGTH_LONG,
        )
    }

    private fun observerUId() {
        businessViewModel.currentUid.observe(this) {
            if (it.isBlank()) {
                startLoginActivity()
                Timber.e("uid is blank")
            } else {
                Timber.e("uid  = $it")
            }
        }
    }

    private fun startLoginActivity() {
        val intent = Intent(this, SignInActivity::class.java)
        // used to clean activity and al activities above it will be removed.
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun observerSteps() {
        observerNextStep()
        observerPreviousStep()
        observerCompleteStep()
    }

    private fun observerNextStep() {
        businessViewModel.buttonNextStep.observe(
            this,
            EventObserver {
                binding.stepper.goToNextStep()
            },
        )
    }

    private fun observerPreviousStep() {
        businessViewModel.buttonPreviousStep.observe(
            this,
            EventObserver {
                binding.stepper.goToPreviousStep()
            },
        )
    }

    private fun observerCompleteStep() {
        businessViewModel.buttonCompletedSteps.observe(
            this,
            EventObserver {
                val intent = Intent(this, MainActivity::class.java)
                // used to clean activity and al activities above it will be removed.
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            },
        )
    }

    private fun observerNetwork() {
        viewModel.setNetworkMonitor()
        viewModel.isOnline.observe(this) {
            binding.isAvailable = it
            Timber.i("IsAvailable = $it")
        }
    }

    override fun onCompleted() {
        binding.stepper.showSnackbar("Step Changed", Snackbar.LENGTH_SHORT)
    }

    override fun onStepChanged(step: Int) {
        binding.stepper.showSnackbar("Step Completed", Snackbar.LENGTH_SHORT)
    }

    override fun onSupportNavigateUp(): Boolean = findNavController(R.id.frame_stepper).navigateUp()

    private fun onSupportBackPressed() {
        onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showAppCloseDialog()
                }
            },
        )
    }

    private fun showAppCloseDialog() {
        MaterialAlertDialogBuilder(this@StepperActivity).setTitle(
            R.string.title_close_stepper_dialog,
        ).setMessage(getString(R.string.message_close_stepper_dialog))
            .setPositiveButton(R.string.positive_close_stepper_dialog) { _, _ ->
                lifecycleScope.launch {
                    val signOutJob = businessViewModel.signOut()
                    signOutJob.join()
                    if (signOutJob.isCompleted) {
                        moveToSignInActivity()
                    }
                }
            }.setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }

    private fun moveToSignInActivity() {
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeObservers()
        _binding = null
    }

    private fun removeObservers() {
        for (field in businessViewModel.javaClass.declaredFields) {
            field.isAccessible = true
            val fieldValue = field.get(businessViewModel)
            if (fieldValue is LiveData<*>) {
                fieldValue.removeObservers(this)
            }
        }
    }
}