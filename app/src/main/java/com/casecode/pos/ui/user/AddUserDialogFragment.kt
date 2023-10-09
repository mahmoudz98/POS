package com.casecode.pos.ui.user

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.casecode.pos.databinding.DialogAddUserBinding
import com.casecode.pos.viewmodel.BusinessViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * A add User Dialog fragment that displays the Add user in Users fragment.
 */
class AddUserDialogFragment : DialogFragment()
{
   companion object
   {
      const val ADD_USER_TAG = "AddUserDialogFragment"
      const val UPDATE_USER_TAG = "UpdateUserDialogFragment"
   }
   
   private var _binding: DialogAddUserBinding? = null
   val binding get() = _binding !!
   private val businessViewModel by activityViewModels<BusinessViewModel>()
   
   override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
   {
      val builder = MaterialAlertDialogBuilder(requireContext())
      _binding = DialogAddUserBinding.inflate(layoutInflater)
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
      
   }
   
   override fun onDestroyView()
   {
      super.onDestroyView()
      _binding = null
   }
}