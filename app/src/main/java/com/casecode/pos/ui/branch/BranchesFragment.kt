package com.casecode.pos.ui.branch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import androidx.slidingpanelayout.widget.SlidingPaneLayout.PanelSlideListener
import com.casecode.pos.adapter.BranchesAdapter
import com.casecode.pos.databinding.FragmentBranchesBinding
import com.casecode.pos.utils.EventObserver
import com.casecode.pos.utils.compactScreen
import com.casecode.pos.utils.showSnackbar
import com.casecode.pos.viewmodel.BusinessViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

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
      init()
      
   }
   
   private fun init()
   {
      initViewModel()
      initAdapter()
      initClicked()
      observerViewModel()
      setupWithTwoPane()
   }
   
   private fun setupWithTwoPane()
   {
      val isCompact = requireActivity().compactScreen();
      businessViewModel.setCompact(isCompact)
      Timber.e("setupTwoPane:  isCompact = %s", isCompact)
      if (!isCompact)
      {
        /*  requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            BranchesOnBackPressedCallback(binding.splBranches))
         binding.splBranches.isOpen */
         val ft: FragmentTransaction = requireFragmentManager() .beginTransaction()
         val dialog = AddBranchesDialogFragment.newInstance()
         ft.add(binding.fcvAddBranch.id, dialog)
         ft.commit()
       //   binding.splBranches.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED
      }else{
        // binding.splBranches.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED
      }
      
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
            val dialog = AddBranchesDialogFragment.newInstance()
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
                  binding.root.showSnackbar(getString(idString), BaseTransientBottomBar.LENGTH_SHORT)
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
                  binding.root.showSnackbar(getString(idString), BaseTransientBottomBar.LENGTH_SHORT)
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
   
   /**
    * Callback providing custom back navigation.
    */
   private class BranchesOnBackPressedCallback internal constructor(private val mSlidingPaneLayout: SlidingPaneLayout) :
      OnBackPressedCallback(mSlidingPaneLayout.isSlideable && mSlidingPaneLayout.isOpen),
      PanelSlideListener
   {
      init
      {
         // Set the default 'enabled' state to true only if it is slideable (i.e., the panes
         // are overlapping) and open (i.e., the detail pane is visible).
         mSlidingPaneLayout.addPanelSlideListener(this)
      }
      
      override fun handleOnBackPressed()
      {
         // Return to the list pane when the system back button is pressed.
         mSlidingPaneLayout.closePane()
      }
      
      override fun onPanelSlide(panel: View, slideOffset: Float)
      {
         //NO thing.
      }
      
      override fun onPanelOpened(panel: View)
      {
         // Intercept the system back button when the detail pane becomes visible.
         isEnabled = true
      }
      
      override fun onPanelClosed(panel: View)
      {
         // Disable intercepting the system back button when the user returns to the
         // list pane.
         isEnabled = false
      }
   }
   
   
}