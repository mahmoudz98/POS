package com.casecode.pos.ui.branch

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.casecode.pos.R
import com.casecode.pos.base.BaseTextWatcher
import com.casecode.pos.databinding.DialogAddBranchesBinding
import com.casecode.pos.utils.showSnackbar
import com.casecode.pos.viewmodel.BusinessViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar

/**
 * A Dialog fragment that displays the Add branch in branches fragment.
 */
class AddBranchesDialogFragment : DialogFragment()
{
   companion object
   {
      const val ADD_BRANCH_TAG = "AddBranchesDialogFragment"
      const val UPDATE_BRANCH_TAG = "updateBranchesDialogFragment"
   }
   
   private var _binding: DialogAddBranchesBinding? = null
   private val binding: DialogAddBranchesBinding
      get() = _binding !!
   private val businessViewModel by activityViewModels<BusinessViewModel>()
   
   override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
   {
      val builder = MaterialAlertDialogBuilder(requireContext())
      _binding = DialogAddBranchesBinding.inflate(layoutInflater)
      
      builder.setView(binding.root)
      
      return builder.create()
   }
   
   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
                            ): View
   {
      // Inflate the layout for this fragment
      
      return binding.root
   }
   
   override fun onViewCreated(view: View, savedInstanceState: Bundle?)
   {
      super.onViewCreated(view, savedInstanceState)
      init()
   }
   
   private fun init()
   {
      validateAddBusiness()
      initAddAndUpdateBusiness()
      if (tag == UPDATE_BRANCH_TAG)
      {
         observerViewModel()
      }
   }
   
   private fun observerViewModel()
   {
      businessViewModel.branchSelected.observe(viewLifecycleOwner) {
         binding.branch = it
      }
   }
   
   private fun validateAddBusiness()
   {
      
      binding.etAddBranchesName.addTextChangedListener(object : BaseTextWatcher()
      {
         override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
         {
            
            if (TextUtils.isEmpty(s))
            {
               binding.tilAddBranchesName.boxStrokeErrorColor
               binding.tilAddBranchesName.error =
                  getString(R.string.add_branch_name_empty)
               
            } else
            {
               binding.tilAddBranchesName.boxStrokeColor =
                  resources.getColor(R.color.md_theme_light_primary, requireActivity().theme)
               binding.tilAddBranchesName.error = null
            }
            
         }
         
      })
      binding.etAddBranchesPhone.addTextChangedListener(object : BaseTextWatcher()
      {
         override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
         {
            
            if (TextUtils.isEmpty(s))
            {
               binding.tilAddBranchesPhone.boxStrokeErrorColor
               binding.tilAddBranchesPhone.error =
                  getString(R.string.add_branch_phone_empty)
               
            } else if (! s.toString().trim { it <= ' ' }
                  .matches(Patterns.PHONE.toString().toRegex()))
            {
               binding.tilAddBranchesPhone.error =
                  getString(R.string.add_branch_phone_invalid)
            } else
            {
               binding.tilAddBranchesPhone.boxStrokeColor =
                  resources.getColor(R.color.md_theme_light_primary, requireActivity().theme)
               binding.tilAddBranchesPhone.error = null
            }
            
         }
         
      })
      
   }
   
   private fun initAddAndUpdateBusiness()
   {
      binding.etAddBranchesPhone.setOnEditorActionListener { _, actionId, _ ->
         if (actionId == EditorInfo.IME_ACTION_DONE)
         {
            binding.btnBranchAdd.performClick()
            return@setOnEditorActionListener true
         }
         false
      }
      
      binding.btnBranchAdd.setOnClickListener {
         
         if (isAddBusinessValid())
         {
            if (tag == ADD_BRANCH_TAG)
            {
               businessViewModel.addBranch()
            } else
            {
               businessViewModel.setUpdateBranch()
            }
            dismiss()
         }
         
      }
   }
   
   private fun isAddBusinessValid(): Boolean
   {
      val name = binding.etAddBranchesName.text.toString()
      val phone = binding.etAddBranchesPhone.text.toString()
      // Check login and pass are empty
      if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)
         ||
         ! phone.trim { it <= ' ' }
            .matches(Patterns.PHONE.toString().toRegex())
      )
      {
         
         if (TextUtils.isEmpty(name))
         {
            binding.tilAddBranchesName.error =
               getString(R.string.add_branch_name_empty)
         }
         if (TextUtils.isEmpty(phone))
         {
            binding.tilAddBranchesPhone.error =
               getString(R.string.add_branch_phone_empty)
         }
         
         if (! phone.trim { it <= ' ' }
               .matches(Patterns.PHONE.toString().toRegex()))
         {
            binding.tilAddBranchesPhone.error =
               getString(R.string.add_branch_phone_invalid)
         }
         
         return false
      }
      businessViewModel.setBranchName(name)
      businessViewModel.setBranchPhone(phone)
      
      return true
      
   }
   
   override fun onDestroyView()
   {
      super.onDestroyView()
      _binding = null
   }
}