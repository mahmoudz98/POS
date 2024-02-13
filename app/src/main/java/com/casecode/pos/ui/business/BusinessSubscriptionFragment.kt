package com.casecode.pos.ui.business

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.casecode.pos.adapter.SubscriptionAdapter
import com.casecode.pos.databinding.FragmentBusinessSubscriptionBinding
import com.casecode.pos.utils.setupSnackbar
import com.casecode.pos.viewmodel.StepperBusinessViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A fragment that displays the business Subscription.
 */
@AndroidEntryPoint
class BusinessSubscriptionFragment : Fragment()
{
   
   private var _binding: FragmentBusinessSubscriptionBinding? = null
   private val binding get() = _binding !!
   internal val businessViewModel by activityViewModels<StepperBusinessViewModel>()
   
   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
                            ): View
   {
      _binding = FragmentBusinessSubscriptionBinding.inflate(inflater, container, false)
      return binding.root
   }
   
   override fun onViewCreated(view: View, savedInstanceState: Bundle?)
   {
      super.onViewCreated(view, savedInstanceState)
      binding.lifecycleOwner = this.viewLifecycleOwner
      init()
   }
   
   private fun init()
   {
      initViewModel()
      initAdapter()
      setupSnackbar()
      initClickListener()
   }
   private fun initViewModel()
   {
      observerNetworkAndGetSubscriptions()
      observerSubscriptions()
      observerDataSubscriptionsIsLoading()
   }
   private fun observerDataSubscriptionsIsLoading()
   {
      businessViewModel.isLoading.observe(viewLifecycleOwner) {
         lifecycleScope.launch {
            if (! it)
            {
               delay(200L)
            }
            binding.isLoading = it
         }
      }
   }
   
   private fun observerSubscriptions()
   {
      businessViewModel.subscriptions.observe(viewLifecycleOwner) {
         
         binding.subscriptions = it;
      }
   }
   
   private fun observerNetworkAndGetSubscriptions()
   {
      businessViewModel.isOnline.observe(viewLifecycleOwner) {
         if (it )
         {
            businessViewModel.getSubscriptionsBusiness()
         }
      }
   }
   
   private fun initAdapter()
   {
      val subscriptionAdapter: SubscriptionAdapter by lazy {
         SubscriptionAdapter {
            businessViewModel.addSubscriptionBusinessSelected(it)
         }
      }
      binding.rvBusinessSubscription.adapter = subscriptionAdapter
   }
   
   private fun setupSnackbar()
   {
      binding.root.setupSnackbar(viewLifecycleOwner,
         businessViewModel.userMessage,
         Snackbar.LENGTH_LONG)
   }
   
   private fun initClickListener()
   {
      binding.btnBusinessSubscriptionEmployee.setOnClickListener {
         businessViewModel.checkNetworkThenSetSubscriptionBusinessSelected()
      }
      binding.btnBusinessSubscriptionBranches.setOnClickListener {
         businessViewModel.moveToPreviousStep()
      }
   }
   
   
   override fun onDestroyView()
   {
      super.onDestroyView()
      _binding = null
   }
   
}