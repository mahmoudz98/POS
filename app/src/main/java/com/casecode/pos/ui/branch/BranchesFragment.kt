package com.casecode.pos.ui.branch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.casecode.domain.utils.Resource
import com.casecode.pos.R
import com.casecode.pos.adapter.BranchesAdapter
import com.casecode.pos.databinding.FragmentBranchesBinding
import com.casecode.pos.utils.asInt
import com.casecode.pos.utils.compactScreen
import com.casecode.pos.utils.setupSnackbar
import com.casecode.pos.utils.showSnackbar
import com.casecode.pos.viewmodel.BusinessViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Fragment responsible for managing and displaying a list of branches in a stepper activity.
 *
 * This fragment is part of the Business setup process, allowing users to add, update, and view branches.
 * It includes UI components such as a RecyclerView for displaying branches and buttons for navigation.
 *
 * @constructor Creates a new instance of BranchesFragment.
 */
@AndroidEntryPoint
class BranchesFragment : Fragment()
{
   private var _binding: FragmentBranchesBinding? = null
   private val binding get() = _binding !!
   
   
   // Shared ViewModel associated with the hosting activity
   internal val businessViewModel by activityViewModels<BusinessViewModel>()
   
   // Lazy initialization of BranchesAdapter
   private val branchAdapter: BranchesAdapter by lazy {
      BranchesAdapter {
         businessViewModel.setBranchSelected(it)
         val dialog = AddBranchesDialogFragment.newInstance()
         dialog.show(parentFragmentManager, AddBranchesDialogFragment.UPDATE_BRANCH_TAG)
      }
   }
   
   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
                            ): View
   {
      _binding = FragmentBranchesBinding.inflate(inflater, container, false)
      
      return binding.root
   }
   
   override fun onViewCreated(view: View, savedInstanceState: Bundle?)
   {
      super.onViewCreated(view, savedInstanceState)
      _binding ?: return // Return early if the binding is null
      init()
      
   }
   
   /**
    * Initializes the ViewModel, Adapter, and click events for the fragment.
    */
   private fun init()
   {
      initViewModel()
      initAdapter()
      initClicked()
      observerUserMessage()
      setupWithTwoPane()
   }
   
   private fun setupWithTwoPane()
   {
      val isCompact = requireActivity().compactScreen()
      businessViewModel.setCompact(isCompact)
      Timber.e("setupTwoPane:  isCompact = %s", isCompact)
      if (! isCompact)
      {
         /*  requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
             BranchesOnBackPressedCallback(binding.splBranches))
          binding.splBranches.isOpen */
         val ft: FragmentTransaction = childFragmentManager.beginTransaction()
         val dialog = AddBranchesDialogFragment.newInstance()
         ft.add(binding.fcvAddBranch.id, dialog)
         ft.commit()
         //   binding.splBranches.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED
      } else
      {
         // binding.splBranches.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED
      }
      
   }
   /**
    * Initializes the ViewModel associated with the layout.
    */
   private fun initViewModel()
   {
      binding.branches.lifecycleOwner = this.viewLifecycleOwner
      binding.branches.viewModel = businessViewModel
   }
   
   
   private fun initAdapter()
   {
      binding.branches.rvBranches.adapter = branchAdapter
   }
   
   /**
    * Initializes click events for buttons and subscription for network availability.
    */
   private fun initClicked()
   {
      initClickSubscription()
      binding.apply {
         btnBranchesInfo.setOnClickListener {
            businessViewModel.moveToPreviousStep()
         }
         branches.btnBranchesAdd.setOnClickListener {
            val dialog = AddBranchesDialogFragment.newInstance()
            dialog.show(parentFragmentManager, AddBranchesDialogFragment.ADD_BRANCH_TAG)
            
         }
      }
      
   }
   
   /**
    * Initializes the subscription to check network availability before adding a business.
    */
   private fun initClickSubscription()
   {
      binding.btnBranchesSubscription.setOnClickListener {
         businessViewModel.setBusiness()
         observerIsAddBusiness()
      }
   }
   
   
   private fun observerIsAddBusiness()
   {
      
      businessViewModel.isAddBusiness.observe(viewLifecycleOwner) {
         when (it)
         {
            is Resource.Success ->
            {
               if (it.data)
               {
                  binding.root.showSnackbar(getString(R.string.add_business_success),
                     Snackbar.LENGTH_SHORT)
                  businessViewModel.moveToNextStep()
               }
               
            }
            
            is Resource.Empty ->
            {
               val messageId = it.message.asInt()
               Timber.e("Empty: ${getString(messageId)}")
               binding.root.showSnackbar(getString(messageId), Snackbar.LENGTH_SHORT)
            }
            
            is Resource.Error ->
            {
               Timber.e("Error: ${it.message}")
               binding.root.showSnackbar(getString(R.string.add_business_failed) + it.message,
                  Snackbar.LENGTH_SHORT)
               
            }
            
            else ->
            {
               binding.root.showSnackbar("Error:${it?.toString()} ", Snackbar.LENGTH_SHORT)
               Timber.e("Error: ${it.toString()}")
            }
         }
      }
   }
   
   /**
    * Observes changes in the ViewModel related to shows corresponding messages.
    */
   private fun observerUserMessage()
   {
      binding.root.setupSnackbar(viewLifecycleOwner, businessViewModel.userMessage, BaseTransientBottomBar.LENGTH_SHORT)
     /*  businessViewModel.userMessage.observe(viewLifecycleOwner) { idString ->
         if (idString != null)
         {
            binding.root.showSnackbar(getString(idString),
               BaseTransientBottomBar.LENGTH_SHORT)
            
         }
      } */
      
   }
   
   override fun onDestroyView()
   {
      super.onDestroyView()
      
      _binding = null
   }
}