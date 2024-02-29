package com.casecode.pos.ui.branch

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.casecode.pos.R
import com.casecode.pos.base.doAfterTextChangedListener
import com.casecode.pos.databinding.DialogAddBranchBinding
import com.casecode.pos.viewmodel.StepperBusinessViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * A Dialog fragment that displays the Add branch in branches fragment.
 */
class AddBranchesDialogFragment : DialogFragment() {
    companion object {
        const val ADD_BRANCH_TAG = "AddBranchesDialogFragment"
        const val UPDATE_BRANCH_TAG = "updateBranchesDialogFragment"

        fun newInstance(): AddBranchesDialogFragment {
            return AddBranchesDialogFragment()
        }
    }

    private var _binding: DialogAddBranchBinding? = null
    private val binding: DialogAddBranchBinding
        get() = _binding!!
    private val businessViewModel by activityViewModels<StepperBusinessViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (businessViewModel.isCompact.value == true) {
            val builder =
                MaterialAlertDialogBuilder(requireContext())
            _binding = DialogAddBranchBinding.inflate(layoutInflater)

            builder.setView(_binding?.root)
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
    ): View? {
        // Inflate the layout for this fragment
        if (businessViewModel.isCompact.value == false) {
            _binding = DialogAddBranchBinding.inflate(layoutInflater, container, false)
        }
        return _binding?.root
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
        validateAddBranch()
        initAddAndUpdateBranch()
        if (tag == UPDATE_BRANCH_TAG) {
            observerViewModel()
        }
    }

    private fun validateAddBranch() {
        binding.etAddBranchesName.doAfterTextChangedListener { branchNameEditText ->

            if (TextUtils.isEmpty(branchNameEditText)) {
                binding.tilAddBranchesName.boxStrokeErrorColor
                binding.tilAddBranchesName.error =
                    getString(R.string.add_branch_name_empty)
            } else {
                binding.tilAddBranchesName.boxStrokeColor =
                    resources.getColor(R.color.md_theme_light_primary, requireActivity().theme)
                binding.tilAddBranchesName.error = null
            }
        }

        binding.etAddBranchesPhone.doAfterTextChangedListener { PhoneEditText ->
            if (TextUtils.isEmpty(PhoneEditText)) {
                binding.tilAddBranchesPhone.boxStrokeErrorColor
                binding.tilAddBranchesPhone.error =
                    getString(R.string.all_phone_empty)
            } else if (!PhoneEditText.toString().trim { it <= ' ' }
                    .matches(Patterns.PHONE.toString().toRegex())
            ) {
                binding.tilAddBranchesPhone.error =
                    getString(R.string.all_phone_invalid)
            } else {
                binding.tilAddBranchesPhone.boxStrokeColor =
                    resources.getColor(R.color.md_theme_light_primary, requireActivity().theme)
                binding.tilAddBranchesPhone.error = null
            }
        }
    }

    private fun initAddAndUpdateBranch() {
        binding.etAddBranchesPhone.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.btnBranch.performClick()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.btnBranch.setOnClickListener {
            if (isValidBranchInput()) {
                if (tag == ADD_BRANCH_TAG) {
                    businessViewModel.addBranch()
                } else {
                    businessViewModel.updateBranch()
                }

                dismissDialogOrClearContext()
            }
        }
    }

    private fun isValidBranchInput(): Boolean {
        val name = binding.etAddBranchesName.text.toString()
        val phone = binding.etAddBranchesPhone.text.toString()
        // Check login and pass are empty
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) ||
            !phone.trim { it <= ' ' }
                .matches(Patterns.PHONE.toString().toRegex())
        ) {
            if (TextUtils.isEmpty(name)) {
                binding.tilAddBranchesName.error =
                    getString(R.string.add_branch_name_empty)
            }
            if (TextUtils.isEmpty(phone)) {
                binding.tilAddBranchesPhone.error =
                    getString(R.string.all_phone_empty)
            }

            if (!phone.trim { it <= ' ' }
                    .matches(Patterns.PHONE.toString().toRegex())
            ) {
                binding.tilAddBranchesPhone.error =
                    getString(R.string.all_phone_invalid)
            }

            return false
        }
        businessViewModel.setBranchName(name)
        businessViewModel.setBranchPhone(phone)

        return true
    }

    private fun dismissDialogOrClearContext() {
        val isCompact = businessViewModel.isCompact.value
        if (isCompact == true) {
            dismiss()
        } else {
            binding.etAddBranchesName.text = null
            binding.etAddBranchesPhone.text = null
        }
    }

    private fun observerViewModel() {
        businessViewModel.branchSelected.observe(viewLifecycleOwner) {
            binding.branch = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}