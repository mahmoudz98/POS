package com.casecode.pos.ui.stepper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.casecode.pos.adapter.EmployeeAdapter
import com.casecode.pos.databinding.FragmentEmployeesStepperBinding
import com.casecode.pos.utils.EventObserver
import com.casecode.pos.utils.compactScreen
import com.casecode.pos.viewmodel.StepperBusinessViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * A fragment that displays a list of employees.
 */
@AndroidEntryPoint
class EmployeesStepperFragment : Fragment() {
    @Suppress("ktlint:standard:property-naming")
    private var _binding: FragmentEmployeesStepperBinding? = null
    val binding get() = _binding!!

    /**
     * The view model for this fragment.
     */
    internal val businessViewModel by activityViewModels<StepperBusinessViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEmployeesStepperBinding.inflate(inflater, container, false)

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
        setupWithTwoPane()
        initViewModel()
        initAdapter()
        initClick()
    }

    /**
     * Sets up the fragment for two-pane mode.
     */
    private fun setupWithTwoPane() {
        val isCompact = requireActivity().compactScreen()
        businessViewModel.setCompact(isCompact)
        if (!isCompact) {
            binding.fcvEmployeesStepperDialog.visibility = View.VISIBLE
            binding.btnEmployeesAdd.visibility = View.GONE
            parentFragmentManager.beginTransaction().replace(
                binding.fcvEmployeesStepperDialog.id,
                EmployeeStepperDialogFragment.newInstance(),
                EmployeeStepperDialogFragment.ADD_EMPLOYEE_STEPPER_TAG,
            ).commit()
            // When Update employee rest ui to add employee in tablet.
            observerUpdateEmployeeInTablet()
        } else {
            binding.fcvEmployeesStepperDialog.visibility = View.GONE
            binding.btnEmployeesAdd.visibility = View.VISIBLE
        }
    }
    /**
     * Initializes the view model.
     */
    private fun initViewModel() {
        businessViewModel.employees.observe(viewLifecycleOwner) {
            binding.employees = it
        }
        businessViewModel.addDefaultEmployee()
    }
    /**
     * Initializes the adapter for the list of employees.
     */
    private fun initAdapter() {
        val employeeAdapter: EmployeeAdapter by lazy {
            EmployeeAdapter {
                businessViewModel.setEmployeeSelected(it)
                if (businessViewModel.isCompact.value == true) {
                    val employeeDialog = EmployeeStepperDialogFragment()
                    employeeDialog.show(
                        parentFragmentManager,
                        EmployeeStepperDialogFragment.UPDATE_EMPLOYEE_STEPPER_TAG,
                    )
                } else {
                    parentFragmentManager.beginTransaction().replace(
                        binding.fcvEmployeesStepperDialog.id,
                        EmployeeStepperDialogFragment.newInstance(),
                        EmployeeStepperDialogFragment.UPDATE_EMPLOYEE_STEPPER_TAG,
                    ).commit()
                }
            }
        }
        binding.rvEmployees.adapter = employeeAdapter
    }

    /**
     * Initializes the click listeners for the buttons.
     */
    private fun initClick() {
        binding.apply {
            btnEmployeesAdd.setOnClickListener {
                val employeeDialog = EmployeeStepperDialogFragment()
                employeeDialog.show(
                    parentFragmentManager,
                    EmployeeStepperDialogFragment.ADD_EMPLOYEE_STEPPER_TAG,
                )
            }
            btnEmployeesStepperSubscription.setOnClickListener {
                businessViewModel.moveToPreviousStep()
            }
            btnEmployeesStepperDone.setOnClickListener {
                businessViewModel.checkNetworkThenSetEmployees()
            }
        }
    }

    /**
     * Observes the update employee event in tablet mode.
     */
    private fun observerUpdateEmployeeInTablet() {
        if (businessViewModel.isCompact.value == false) {
            businessViewModel.isUpdateEmployee.observe(
                viewLifecycleOwner,
                EventObserver {
                    // Clear previous update employee dialog.
                    parentFragmentManager.beginTransaction().replace(
                        binding.fcvEmployeesStepperDialog.id,
                        EmployeeStepperDialogFragment.newInstance(),
                        EmployeeStepperDialogFragment.ADD_EMPLOYEE_STEPPER_TAG,
                    ).commit()
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}