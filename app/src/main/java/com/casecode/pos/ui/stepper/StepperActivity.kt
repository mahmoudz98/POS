package com.casecode.pos.ui.stepper

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.aceinteract.android.stepper.StepperNavListener
import com.casecode.pos.R
import com.casecode.pos.databinding.ActivityStepperBinding
import com.casecode.data.utils.NetworkConnection
import com.casecode.pos.utils.EventObserver
import com.casecode.pos.utils.showSnackbar
import com.casecode.pos.viewmodel.BusinessViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class StepperActivity : AppCompatActivity(), StepperNavListener
{
   
   private var _binding: ActivityStepperBinding? = null
   private val binding: ActivityStepperBinding get() = _binding !!
   
   @Inject
   lateinit var networkConnection: NetworkConnection
   
   private val businessViewModel by viewModels<BusinessViewModel>()
  internal val viewModel : BusinessViewModel get() = businessViewModel
   
   override fun onCreate(savedInstanceState: Bundle?)
   {
      super.onCreate(savedInstanceState)
      _binding = ActivityStepperBinding.inflate(layoutInflater)
      setContentView(_binding?.root)
      
      binding.stepper.setupWithNavController(findNavController(R.id.frame_stepper))
      observerNextStep()
      observerPreviousStep()
      observerNetwork()
      
   }
   
   private  fun observerNextStep(){
      businessViewModel.buttonNextStep.observe(this, EventObserver{
      binding.stepper.goToNextStep()
      })
   }
   private  fun observerPreviousStep(){
      businessViewModel.buttonPreviousStep.observe(this, EventObserver{
         binding.stepper.goToPreviousStep()
      })
   }
   
   private fun observerNetwork()
   {
      lifecycleScope.launch {
         networkConnection.isAvailable.collect {
            binding.isAvailable = it
            Timber.d("IsAvailable = $it")
            
         }
      }
    
   }
  
   private fun setupSnackbar() {
   //   view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
   }
   override fun onCompleted()
   {
      binding.stepper.showSnackbar("Step Changed", Snackbar.LENGTH_SHORT)
    
   }
   
   override fun onStepChanged(step: Int)
   {
      binding.stepper.showSnackbar("Step Completed", Snackbar.LENGTH_SHORT)
   }
   
   override fun onSupportNavigateUp(): Boolean = findNavController(R.id.frame_stepper).navigateUp()
   
   override fun onDestroy()
   {
      super.onDestroy()
      networkConnection.onDestroy()
      
      // removeObservers()
      _binding = null
      
   }
   
   private fun removeObservers()
   {
      for (field in networkConnection.javaClass.declaredFields)
      {
         field.isAccessible = true
         val fieldValue = field.get(networkConnection)
         if (fieldValue is LiveData<*>)
         {
            fieldValue.removeObservers(this)
         }
      }
      
   }
}