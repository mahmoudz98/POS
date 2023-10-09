package com.casecode.pos.ui.branch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.casecode.pos.adapter.BranchesAdapter
import com.casecode.pos.databinding.FragmentBranchesBinding
import com.casecode.pos.utils.EventObserver
import com.casecode.pos.utils.showSnackbar
import com.casecode.pos.viewmodel.BusinessViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import dagger.hilt.android.AndroidEntryPoint

/**
 * Branches fragment that displays in stepper activity.
 */
@AndroidEntryPoint
class BranchesFragment : Fragment()
{
   private var _binding: FragmentBranchesBinding? = null
   private val binding get() = _binding !!
   internal val businessViewModel by activityViewModels<BusinessViewModel>()
   
   private val branchAdapter: BranchesAdapter by lazy {
      BranchesAdapter {
         businessViewModel.setBranchSelected(it)
         val dialog = AddBranchesDialogFragment()
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
      init()
      
   }
   
   private fun init()
   {
      initViewModel()
      initAdapter()
      initClicked()
      observerViewModel()
   }
   

   
   private fun initViewModel()
   {
      binding.branches.lifecycleOwner = this.viewLifecycleOwner
      binding.branches.viewModel = businessViewModel
   }
   
   
   private fun initAdapter()
   {
      binding.branches.rvBranches.adapter = branchAdapter
      
   }
   
   
   private fun initClicked()
   {
      binding.apply {
         btnBranchesPlan.setOnClickListener {
            if (businessViewModel.branches.value?.isEmpty() == false)
            {
               businessViewModel.setBusiness()
               businessViewModel.moveToNextStep()
               
            }
         }
         
         btnBranchesInfo.setOnClickListener {
            businessViewModel.moveToPreviousStep()
            
         }
         
         branches.btnBranchesAdd.setOnClickListener {
            val dialog = AddBranchesDialogFragment()
            dialog.show(parentFragmentManager, AddBranchesDialogFragment.ADD_BRANCH_TAG)
            
         }
      }
      
   }
   private fun observerViewModel()
   {
      businessViewModel.isAddBranch.observe(viewLifecycleOwner, EventObserver { isAdd ->
         if (isAdd)
         {
            businessViewModel.userMessage.observe(viewLifecycleOwner) { idString ->
               if (idString != null)
               {
                  binding.root.showSnackbar(getString(idString), BaseTransientBottomBar.LENGTH_LONG)
                  businessViewModel.snackbarMessageShown()
                  
               }
            }
            
            
         }
      })
      
      businessViewModel.isUpdateBranch.observe(viewLifecycleOwner, EventObserver { isUpdate ->
         if (isUpdate)
         {
            businessViewModel.userMessage.observe(viewLifecycleOwner) { idString ->
               if (idString != null)
               {
                  binding.root.showSnackbar(getString(idString), BaseTransientBottomBar.LENGTH_LONG)
                  businessViewModel.snackbarMessageShown()
                  
               }
            }
            
            
         }
      })
   }
   override fun onDestroyView()
   {
      super.onDestroyView()
      
      _binding = null
   }
   
   
}