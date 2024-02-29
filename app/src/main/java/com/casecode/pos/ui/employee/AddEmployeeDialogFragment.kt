package com.casecode.pos.ui.employee

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
import com.casecode.pos.databinding.DialogAddEmployeeBinding
import com.casecode.pos.viewmodel.StepperBusinessViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * A add Employee Dialog fragment that displays the Add Employee in Users fragment.
 */
class AddEmployeeDialogFragment : DialogFragment() {
    companion object {
        const val ADD_EMPLOYEE_TAG = "AddEmployeeDialogFragment"
        const val UPDATE_EMPLOYEE_TAG = "UpdateEmployeeDialogFragment"

        fun newInstance(): AddEmployeeDialogFragment {
            return AddEmployeeDialogFragment()
        }
    }

    private var _binding: DialogAddEmployeeBinding? = null
    val binding get() = _binding!!
    private val businessViewModel by activityViewModels<StepperBusinessViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (businessViewModel.isCompact.value == true) {
            val builder = MaterialAlertDialogBuilder(requireContext())
            _binding = DialogAddEmployeeBinding.inflate(layoutInflater)
            builder.setView(binding.root)
            return builder.create()
        } else {
            val dialog = super.onCreateDialog(savedInstanceState)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            return dialog
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        if (businessViewModel.isCompact.value == false) {
            _binding = DialogAddEmployeeBinding.inflate(layoutInflater, container, false)
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
        initAddOrUpdateEmployee()
        if (tag == UPDATE_EMPLOYEE_TAG) {
            observerEmployeeSelected()
        }
    }

    private fun initViewModel() {
        binding.branches = businessViewModel.branches.value
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
        binding.etAddEmployeeName.doAfterTextChangedListener { nameEditText ->
            if (TextUtils.isEmpty(nameEditText)) {
                binding.tilAddEmployeeName.boxStrokeErrorColor
                binding.tilAddEmployeeName.error =
                    getString(R.string.add_employee_name_empty)
            } else {
                binding.tilAddEmployeeName.boxStrokeColor =
                    resources.getColor(R.color.md_theme_light_primary, requireActivity().theme)
                binding.tilAddEmployeeName.error = null
            }
        }
    }

    private fun validatePhoneEmployeeInput() {
        binding.etAddEmployeePhone.doAfterTextChangedListener { phoneEditText ->

            if (TextUtils.isEmpty(phoneEditText)) {
                binding.tilAddEmployeePhone.boxStrokeErrorColor
                binding.tilAddEmployeePhone.error =
                    getString(R.string.all_phone_empty)
            } else if (!phoneEditText.toString().trim { it <= ' ' }
                    .matches(Patterns.PHONE.toString().toRegex())
            ) {
                binding.tilAddEmployeePhone.error =
                    getString(R.string.all_phone_invalid)
            } else {
                binding.tilAddEmployeePhone.boxStrokeColor =
                    resources.getColor(R.color.md_theme_light_primary, requireActivity().theme)
                binding.tilAddEmployeePhone.error = null
            }
        }
    }

    private fun validatePasswordEmployeeInput() {
        binding.etAddEmployeePassword.doAfterTextChangedListener { passwordEditText ->

            if (TextUtils.isEmpty(passwordEditText)) {
                binding.tilAddEmployeePassword.boxStrokeErrorColor
                binding.tilAddEmployeePassword.error =
                    getString(R.string.add_employee_password_empty)
            } else if (passwordEditText.toString().length < 6) {
                binding.tilAddEmployeePassword.error =
                    getString(R.string.add_employee_password_error)
            } else {
                binding.tilAddEmployeePassword.boxStrokeColor =
                    resources.getColor(R.color.md_theme_light_primary, requireActivity().theme)
                binding.tilAddEmployeePassword.error = null
            }
        }
    }

    private fun initAddOrUpdateEmployee() {
        binding.btnEmployee.setOnClickListener {
            if (isValidEmployeeInput()) {
                dismissDialog()
            }
        }
    }

    private fun isValidEmployeeInput(): Boolean {
        val name = binding.etAddEmployeeName.text.toString()
        val phone = binding.etAddEmployeePhone.text.toString()
        val password = binding.etAddEmployeePassword.text.toString()
        val branchName = binding.actvEmployeeBranch.text.toString()
        val permission = binding.actvEmployeePermission.text.toString()

        if (checkIsValidEmployee(name, phone, password, branchName, permission)) return false

        if (tag == ADD_EMPLOYEE_TAG) {
            businessViewModel.addEmployee(name, phone, password, branchName, permission)
        } else {
            businessViewModel.updateEmployee(name, phone, password, branchName, permission)
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
        if (name.isBlank() || phone.isBlank() || !phone.matches(Patterns.PHONE.toRegex()) ||
            password.isBlank() || password.length < 6 || branchName.isBlank() ||
            permission.isBlank()
        ) {
            if (name.isBlank()) {
                binding.tilAddEmployeeName.error = getString(R.string.add_employee_name_empty)
            }
            if (phone.isBlank()) {
                binding.tilAddEmployeePhone.error = getString(R.string.all_phone_empty)
            } else if (!phone.matches(Patterns.PHONE.toRegex())) {
                binding.tilAddEmployeePhone.error = getString(R.string.all_phone_invalid)
            }

            if (password.isBlank()) {
                binding.tilAddEmployeePassword.error =
                    getString(R.string.add_employee_password_empty)
            } else if (password.length < 6) {
                binding.tilAddEmployeePassword.error =
                    getString(R.string.add_employee_password_error)
            }

            if (branchName.isBlank()) {
                binding.tilAddEmployeeBranch.error = getString(R.string.add_employee_branch_empty)
            }
            if (permission.isBlank()) {
                binding.tilAddEmployeePermission.error =
                    getString(R.string.add_employee_permission_empty)
            }

            return true
        }
        return false
    }

    private fun dismissDialog() {
        val isCompact = businessViewModel.isCompact.value
        if (isCompact == true) {
            dismiss()
        } else {
            binding.etAddEmployeeName.text = null
            binding.etAddEmployeePhone.text = null
            binding.etAddEmployeePassword.text = null
            binding.actvEmployeeBranch.text = null
            binding.actvEmployeePermission.text = null
        }
    }

    private fun observerEmployeeSelected() {
        businessViewModel.employeeSelected.observe(viewLifecycleOwner) {
            binding.employee = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}