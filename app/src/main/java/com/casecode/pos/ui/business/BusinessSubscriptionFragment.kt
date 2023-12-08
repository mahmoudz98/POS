package com.casecode.pos.ui.business

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.casecode.domain.utils.Resource
import com.casecode.pos.adapter.SubscriptionAdapter
import com.casecode.pos.databinding.FragmentBusinessSubscriptionBinding
import com.casecode.pos.utils.EventObserver
import com.casecode.pos.utils.showSnackbar
import com.casecode.pos.viewmodel.BusinessViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * A fragment that displays the business Subscription.
 */
@AndroidEntryPoint
class BusinessSubscriptionFragment : Fragment()
{
   
   private var _binding: FragmentBusinessSubscriptionBinding? = null
   private val binding get() = _binding !!
   private val businessViewModel by activityViewModels<BusinessViewModel>()
 
   private val subscriptionAdapter: SubscriptionAdapter by lazy {
      SubscriptionAdapter {
         Timber.e("click on Subscription: $it")
         Timber.d("Current thread adapter = ${Thread.currentThread()}")
         businessViewModel.setSubscriptionBusinessSelected(it)
      }
   }
   
   
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
      init()
   }
   
   private fun init()
   {
      initViewModel()
      initAdapter()
      initClickListener()
   }
   
   private fun initViewModel()
   {
      businessViewModel.getSubscriptionsBusiness()
      
      binding.lifecycleOwner = this.viewLifecycleOwner
      binding.viewModel = businessViewModel
   }
   
   private fun initAdapter()
   {
      binding.rvBusinessSubscription.adapter = subscriptionAdapter
      
   }
   
   private fun observerIsAddSubscription(){
      businessViewModel.isAddSubscriptionBusiness.observe(viewLifecycleOwner, EventObserver{
         when(it){
            is Resource.Success ->{
               businessViewModel.moveToNextStep()
               
            }else ->{
               binding.root.showSnackbar("Something went wrong !", Snackbar.LENGTH_SHORT)
            }
         }
      })
   }
   
   private fun initClickListener()
   {
      binding.btnBusinessSubscriptionUser.setOnClickListener {
         businessViewModel.setSubscriptionBusinessSelected()
         observerIsAddSubscription()
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