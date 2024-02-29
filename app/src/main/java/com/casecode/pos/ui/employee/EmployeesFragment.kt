package com.casecode.pos.ui.employee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.casecode.pos.adapter.EmployeeAdapter
import com.casecode.pos.databinding.FragmentEmployeesBinding
import com.casecode.pos.utils.EventObserver
import com.casecode.pos.utils.compactScreen
import com.casecode.pos.viewmodel.StepperBusinessViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmployeesFragment : Fragment() {
    private var _binding: FragmentEmployeesBinding? = null
    val binding get() = _binding!!

    internal val businessViewModel by activityViewModels<StepperBusinessViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEmployeesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this.viewLifecycleOwner
        init()
    }

    private fun init() {
        initViewModel()
        initAdapter()
        initClick()
        setupWithTwoPane()
    }

    private fun setupWithTwoPane() {
        val isCompact = requireActivity().compactScreen()
        businessViewModel.setCompact(isCompact)
        if (!isCompact) {
            binding.fcvEmployeesDialog.visibility = View.VISIBLE
            binding.lEmployees.btnEmployeesAdd.visibility = View.GONE
            parentFragmentManager.beginTransaction().replace(
                binding.fcvEmployeesDialog.id,
                AddEmployeeDialogFragment.newInstance(),
                AddEmployeeDialogFragment.ADD_EMPLOYEE_TAG,
            ).commit()
            // When Update employee rest ui to add employee in tablet.
            observerUpdateEmployeeInTablet()
        } else {
            binding.fcvEmployeesDialog.visibility = View.GONE
            binding.lEmployees.btnEmployeesAdd.visibility = View.VISIBLE
        }
    }

    private fun initViewModel() {
        businessViewModel.employees.observe(viewLifecycleOwner) {
            binding.lEmployees.employees = it
        }
        businessViewModel.addDefaultEmployee()
    }

    private fun initAdapter() {
        val employeeAdapter: EmployeeAdapter by lazy {
            EmployeeAdapter {
                businessViewModel.setEmployeeSelected(it)
                if (businessViewModel.isCompact.value == true) {
                    val employeeDialog = AddEmployeeDialogFragment()
                    employeeDialog.show(
                        parentFragmentManager,
                        AddEmployeeDialogFragment.UPDATE_EMPLOYEE_TAG,
                    )
                } else {
                    parentFragmentManager.beginTransaction().replace(
                        binding.fcvEmployeesDialog.id,
                        AddEmployeeDialogFragment.newInstance(),
                        AddEmployeeDialogFragment.UPDATE_EMPLOYEE_TAG,
                    ).commit()
                }
            }
        }
        binding.lEmployees.rvEmployees.adapter = employeeAdapter
    }

    private fun initClick() {
        binding.apply {
            lEmployees.btnEmployeesAdd.setOnClickListener {
                val employeeDialog = AddEmployeeDialogFragment()
                employeeDialog.show(
                    parentFragmentManager,
                    AddEmployeeDialogFragment.ADD_EMPLOYEE_TAG,
                )
            }
            btnEmployeesSubscription.setOnClickListener {
                businessViewModel.moveToPreviousStep()
            }
            btnEmployeesDone.setOnClickListener {
                businessViewModel.checkNetworkThenSetEmployees()
            }
        }
    }

    private fun observerUpdateEmployeeInTablet() {
        if (businessViewModel.isCompact.value == false) {
            businessViewModel.isUpdateEmployee.observe(
                viewLifecycleOwner,
                EventObserver {
                    // Clear previous update employee dialog.
                    parentFragmentManager.beginTransaction().replace(
                        binding.fcvEmployeesDialog.id,
                        AddEmployeeDialogFragment.newInstance(),
                        AddEmployeeDialogFragment.ADD_EMPLOYEE_TAG,
                    ).commit()
                },
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}
