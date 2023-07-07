package com.casecode.pos.ui.signout

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.casecode.pos.databinding.DialogSignOutBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class SignOutDialog : DialogFragment() {


    private var _binding: DialogSignOutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.ivSignOutClose.setOnClickListener {
            dismiss()
        }
        isCancelable = false
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(
            requireContext()

        )
        _binding = DialogSignOutBinding.inflate(layoutInflater)

        builder.setView(binding.root)
        builder.setCancelable(false)

        return builder.create()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}