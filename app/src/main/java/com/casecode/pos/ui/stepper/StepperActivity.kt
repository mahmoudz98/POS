package com.casecode.pos.ui.stepper

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.aceinteract.android.stepper.StepperNavListener
import com.casecode.pos.R
import com.casecode.pos.databinding.ActivityStepperBinding

class StepperActivity : AppCompatActivity(), StepperNavListener {


    private var binding: ActivityStepperBinding? = null
    private val _binding: ActivityStepperBinding get() = binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStepperBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _binding.stepper.setupWithNavController(findNavController(R.id.frame_stepper))
    }

    fun getNextStep() {
        _binding.stepper.goToNextStep()
    }

    fun getPreviousStep() {
        _binding.stepper.goToPreviousStep()
    }


    override fun onCompleted() {

        Toast.makeText(
            this,
            "Step changed to: ${_binding.stepper.goToNextStep()}",
            Toast.LENGTH_SHORT
        ).show()


    }

    override fun onStepChanged(step: Int) {
        Toast.makeText(this, "Stepper completed", Toast.LENGTH_SHORT).show()

    }

    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.frame_stepper).navigateUp()
}