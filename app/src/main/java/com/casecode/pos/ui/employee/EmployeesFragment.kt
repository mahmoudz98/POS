package com.casecode.pos.ui.employee

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.casecode.pos.adapter.EmployeeAdapter
import com.casecode.pos.databinding.FragmentEmployeesBinding
import com.casecode.pos.utils.EventObserver
import com.casecode.pos.utils.compactScreen
import com.casecode.pos.utils.setupSnackbar
import com.casecode.pos.viewmodel.EmployeeViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmployeesFragment : Fragment() {
    private var _binding: FragmentEmployeesBinding? = null
    val binding get() = _binding!!
    private val viewModel : EmployeeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentEmployeesBinding.inflate(inflater, container, false)
        return _binding?.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this.viewLifecycleOwner
        setup()
    }

    private fun setup() {
        setupWithTwoPane()
        setupObserver()
        setupAdapter()
        setupClick()
    }
    private fun setupObserver(){
        viewModel.employees.observe(viewLifecycleOwner) {
            binding.employees = it
        }
        viewModel.isEmptyEmployees.observe(viewLifecycleOwner) {
            binding.isEmpty = it
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.isLoading = it
        }
        binding.root.setupSnackbar(viewLifecycleOwner, viewModel.userMessage, Snackbar.LENGTH_SHORT)

    }
    private fun setupWithTwoPane() {
        val isCompact = requireActivity().compactScreen()
        viewModel.setCompact(isCompact)
        if (!isCompact) {
            binding.fcvEmployeesDialog.visibility = View.VISIBLE
            binding.btnEmployeesAdd.visibility = View.GONE
            childFragmentManager.beginTransaction().replace(
                binding.fcvEmployeesDialog.id,
                EmployeeDialogFragment.newInstance(),
                EmployeeDialogFragment.ADD_EMPLOYEE_TAG,
            ).commit()
            // When Update employee rest ui to add employee in tablet.
            observerUpdateEmployeeInTablet()
        } else {
            binding.fcvEmployeesDialog.visibility = View.GONE
            binding.btnEmployeesAdd.visibility = View.VISIBLE
        }
    }
    private fun observerUpdateEmployeeInTablet() {
        if (viewModel.isCompact.value == false) {
           viewModel.isUpdateEmployee.observe(
                viewLifecycleOwner,
                EventObserver {
                    // Clear previous update employee dialog.
                    childFragmentManager.beginTransaction().replace(
                        binding.fcvEmployeesDialog.id,
                        EmployeeDialogFragment.newInstance(),
                        EmployeeDialogFragment.ADD_EMPLOYEE_TAG,
                    ).commit()
                }
            )
        }
    }

    private fun setupClick(){
        binding.btnEmployeesAdd.setOnClickListener {
            val employeeDialog = EmployeeDialogFragment()
            employeeDialog.show(
                childFragmentManager,
                EmployeeDialogFragment.ADD_EMPLOYEE_TAG,
            )
        }
    }
    private fun setupAdapter() {
        val employeeAdapter: EmployeeAdapter by lazy {
            EmployeeAdapter {
                viewModel.setEmployeeSelected(it)
                if (viewModel.isCompact.value == true) {
                    val employeeDialog = EmployeeDialogFragment()
                    employeeDialog.show(
                        childFragmentManager,
                        EmployeeDialogFragment.UPDATE_EMPLOYEE_TAG,
                    )
                } else {
                    childFragmentManager.beginTransaction().replace(
                        binding.fcvEmployeesDialog.id,
                        EmployeeDialogFragment.newInstance(),
                        EmployeeDialogFragment.UPDATE_EMPLOYEE_TAG,
                    ).commit()
                }
            }
        }
        binding.rvEmployees.adapter = employeeAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        viewModelStore.clear()
    }


}