package com.casecode.pos.ui.employee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.casecode.pos.adapter.EmployeeAdapter
import com.casecode.pos.databinding.FragmentEmployeesBinding
import com.casecode.pos.utils.setupSnackbar
import com.casecode.pos.viewmodel.BusinessViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmployeesFragment : Fragment()
{
   
   
   private var _binding: FragmentEmployeesBinding? = null
   val binding get() = _binding !!
   
   
   internal val businessViewModel by activityViewModels<BusinessViewModel>()
   
   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
                            ): View
   {
      _binding = FragmentEmployeesBinding.inflate(inflater, container, false)
      
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
      setupSnackbar()
      initAdapter()
      initClick()
   }
   
   private fun initViewModel()
   {
      binding.lEmployees.viewModel = businessViewModel
      businessViewModel.addDefaultEmployee()
   }
   
   private fun setupSnackbar()
   {
      binding.root.setupSnackbar(viewLifecycleOwner,
         businessViewModel.userMessage,
         BaseTransientBottomBar.LENGTH_SHORT)
   }
   
   private fun initAdapter()
   {
      val employeeAdapter: EmployeeAdapter by lazy {
         EmployeeAdapter {
            businessViewModel.setEmployeeSelected(it)
            val employeeDialog = AddEmployeeDialogFragment()
            employeeDialog.show(parentFragmentManager,
               AddEmployeeDialogFragment.UPDATE_EMPLOYEE_TAG)
         }
      }
      binding.lEmployees.rvEmployees.adapter = employeeAdapter
   }
   
   private fun initClick()
   {
      binding.apply {
         lEmployees.btnEmployeesAdd.setOnClickListener {
            val employeeDialog = AddEmployeeDialogFragment()
            employeeDialog.show(parentFragmentManager, AddEmployeeDialogFragment.ADD_EMPLOYEE_TAG)
         }
         
         btnEmployeesSubscription.setOnClickListener {
            businessViewModel.moveToPreviousStep()
         }
         btnEmployeesDone.setOnClickListener {
            businessViewModel.checkNetworkThenSetEmployees()
         }
      }
      
   }
   
   
   override fun onDestroyView()
   {
      super.onDestroyView()
      
      _binding = null
   }
   
   
}