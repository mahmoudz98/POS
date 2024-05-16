package com.casecode.pos.ui.stepper

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.casecode.pos.R
import com.casecode.pos.base.doAfterTextChangedListener
import com.casecode.pos.databinding.DialogEmployeeBinding
import com.casecode.pos.viewmodel.StepperBusinessViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * A add Employee Dialog fragment that displays the  Employee in Users fragment.
 */
class EmployeeStepperDialogFragment : DialogFragment() {
    companion object {
        const val ADD_EMPLOYEE_STEPPER_TAG = "EmployeesStepperDialogFragment"
        const val UPDATE_EMPLOYEE_STEPPER_TAG = "UpdateEmployeeStepperDialogFragment"

        fun newInstance(): EmployeeStepperDialogFragment {
            return EmployeeStepperDialogFragment()
        }
    }

    @Suppress("ktlint:standard:property-naming")
    private var _binding: DialogEmployeeBinding? = null
    val binding get() = _binding!!
    private val viewModel by activityViewModels<StepperBusinessViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return if (viewModel.isCompact.value == true) {
            val builder = MaterialAlertDialogBuilder(requireContext())
            _binding = DialogEmployeeBinding.inflate(layoutInflater)
            builder.setView(binding.root)
            builder.create()
        } else {
            val dialog = super.onCreateDialog(savedInstanceState)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        if (viewModel.isCompact.value == false) {
            _binding = DialogEmployeeBinding.inflate(layoutInflater, container, false)
        }
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
        validateInputEmployee()
        setupClick()
        if (tag == UPDATE_EMPLOYEE_STEPPER_TAG) {
            observerEmployeeSelected()
        }
    }

    private fun initViewModel() {
        binding.branches = viewModel.branches.value
    }

    /**
     * validate for Name, phone number, password, branch name , permission
     */
    private fun validateInputEmployee() {
        validateNameEmployeeInput()
        validatePhoneEmployeeInput()
        validatePasswordEmployeeInput()
    }

    private fun validateNameEmployeeInput() {
        binding.etEmployeeName.doAfterTextChangedListener { nameEditText ->
            if (TextUtils.isEmpty(nameEditText)) {
                binding.tilEmployeeName.boxStrokeErrorColor
                binding.tilEmployeeName.error = getString(R.string.add_employee_name_empty)
            } else {
                binding.tilEmployeeName.boxStrokeColor =
                    resources.getColor(R.color.md_theme_light_primary, requireActivity().theme)
                binding.tilEmployeeName.error = null
            }
        }
    }

    private fun validatePhoneEmployeeInput() {
        binding.etEmployeePhone.doAfterTextChangedListener { phoneEditText ->

            if (TextUtils.isEmpty(phoneEditText)) {
                binding.tilEmployeePhone.boxStrokeErrorColor
                binding.tilEmployeePhone.error = getString(R.string.all_phone_empty)
            } else if (!phoneEditText.toString().trim { it <= ' ' }
                    .matches(Patterns.PHONE.toString().toRegex())) {
                binding.tilEmployeePhone.error = getString(R.string.all_phone_invalid)
            } else {
                binding.tilEmployeePhone.boxStrokeColor =
                    resources.getColor(R.color.md_theme_light_primary, requireActivity().theme)
                binding.tilEmployeePhone.error = null
            }
        }
    }

    private fun validatePasswordEmployeeInput() {
        binding.etEmployeePassword.doAfterTextChangedListener { passwordEditText ->

            if (TextUtils.isEmpty(passwordEditText)) {
                binding.tilEmployeePassword.boxStrokeErrorColor
                binding.tilEmployeePassword.error =
                    getString(R.string.add_employee_password_empty)
            } else if (passwordEditText.toString().length < 6) {
                binding.tilEmployeePassword.error =
                    getString(R.string.add_employee_password_error)
            } else {
                binding.tilEmployeePassword.boxStrokeColor =
                    resources.getColor(R.color.md_theme_light_primary, requireActivity().theme)
                binding.tilEmployeePassword.error = null
            }
        }
    }

    private fun setupClick() {
        binding.btnEmployee.setOnClickListener {
            if (isValidEmployeeInput()) {
                dismissDialog()
            }
        }
    }

    private fun isValidEmployeeInput(): Boolean {
        val name = binding.etEmployeeName.text.toString()
        val phone = binding.etEmployeePhone.text.toString()
        val password = binding.etEmployeePassword.text.toString()
        val branchName = binding.actvEmployeeBranch.text.toString()
        val permission = binding.actvEmployeePermission.text.toString()

        if (checkIsValidEmployee(name, phone, password, branchName, permission)) return false

        if (tag == ADD_EMPLOYEE_STEPPER_TAG) {
            viewModel.addEmployee(name, phone, password, branchName, permission)
        } else {
            viewModel.updateEmployee(name, phone, password, branchName, permission)
        }
        return true
    }

    private fun checkIsValidEmployee(
        name: String,
        phone: String,
        password: String,
        branchName: String,
        permission: String,
    ): Boolean {
        if (name.isBlank() || phone.isBlank() || !phone.matches(Patterns.PHONE.toRegex()) || password.isBlank() || password.length < 6 || branchName.isBlank() || permission.isBlank()) {
            if (name.isBlank()) {
                binding.tilEmployeeName.error = getString(R.string.add_employee_name_empty)
            }
            if (phone.isBlank()) {
                binding.tilEmployeePhone.error = getString(R.string.all_phone_empty)
            } else if (!phone.matches(Patterns.PHONE.toRegex())) {
                binding.tilEmployeePhone.error = getString(R.string.all_phone_invalid)
            }

            if (password.isBlank()) {
                binding.tilEmployeePassword.error =
                    getString(R.string.add_employee_password_empty)
            } else if (password.length < 6) {
                binding.tilEmployeePassword.error =
                    getString(R.string.add_employee_password_error)
            }

            if (branchName.isBlank()) {
                binding.tilEmployeeBranch.error = getString(R.string.add_employee_branch_empty)
            }
            if (permission.isBlank()) {
                binding.tilEmployeePermission.error =
                    getString(R.string.add_employee_permission_empty)
            }

            return true
        }
        return false
    }

    private fun dismissDialog() {
        val isCompact = viewModel.isCompact.value
        if (isCompact == true) {
            dismiss()
        } else {
            binding.etEmployeeName.text = null
            binding.etEmployeePhone.text = null
            binding.etEmployeePassword.text = null
            binding.actvEmployeeBranch.text = null
            binding.actvEmployeePermission.text = null
        }
    }

    private fun observerEmployeeSelected() {
        viewModel.employeeSelected.observe(viewLifecycleOwner) {
            binding.employee = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}