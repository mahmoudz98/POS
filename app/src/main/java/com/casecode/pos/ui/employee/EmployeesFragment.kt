package com.casecode.pos.ui.employee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.casecode.domain.utils.Resource
import com.casecode.pos.adapter.EmployeeAdapter
import com.casecode.pos.databinding.FragmentEmployeesBinding
import com.casecode.pos.utils.EventObserver
import com.casecode.pos.utils.setupSnackbar
import com.casecode.pos.utils.showSnackbar
import com.casecode.pos.viewmodel.BusinessViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
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
      init()
      
   }
   
   private fun init()
   {
      initViewModel()
      observerViewModel()
      initAdapter()
      initClick()
   }
   
   private fun initViewModel()
   {
      binding.lifecycleOwner = this.viewLifecycleOwner
      binding.lEmployees.viewModel = businessViewModel
   }
   
   private fun observerViewModel(){
      binding.root.setupSnackbar(viewLifecycleOwner, businessViewModel.userMessage, BaseTransientBottomBar.LENGTH_SHORT)
      
      /*  businessViewModel.isAddEmployee.observe(viewLifecycleOwner, EventObserver { isAdd ->
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
       businessViewModel.isUpdateEmployee.observe(viewLifecycleOwner, EventObserver{isUpdate->
          if(isUpdate){
             businessViewModel.userMessage.observe(viewLifecycleOwner){idString->
                if(idString != null){
                   binding.root.showSnackbar(getString(idString), BaseTransientBottomBar.LENGTH_SHORT)
                   businessViewModel.snackbarMessageShown()
                }
             }
          }
       }) */
   }
   private fun initAdapter()
   {
       val employeeAdapter : EmployeeAdapter by lazy {
         EmployeeAdapter{
            businessViewModel.setEmployeeSelected(it)
            val employeeDialog = AddEmployeeDialogFragment()
            employeeDialog.show(parentFragmentManager, AddEmployeeDialogFragment.UPDATE_EMPLOYEE_TAG)
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
          businessViewModel.setEmployees()
            observerIsAddEmployees()
         }
      }
      
   }
   private fun observerIsAddEmployees(){
      businessViewModel.isAddEmployees.observe(viewLifecycleOwner, EventObserver{ it ->
         when(it){
            is Resource.Success ->{
               businessViewModel.completedSteps()
            }else ->{
             /*   businessViewModel.userMessage.observe(viewLifecycleOwner){idString->
                  if(idString!= null)
               binding.root.showSnackbar(getString(idString), Snackbar.LENGTH_SHORT)
               } */
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