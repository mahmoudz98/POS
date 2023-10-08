package com.casecode.pos.ui.branch

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.casecode.pos.databinding.DialogAddBranchesBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddBranchesDialogFragment : DialogFragment() {

    private lateinit var binding: DialogAddBranchesBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext())
        binding = DialogAddBranchesBinding.inflate(layoutInflater)

        builder.setView(binding.root)

        return builder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        return binding.root
    }

}