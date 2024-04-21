package com.casecode.pos.ui.item

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.casecode.pos.R
import com.casecode.pos.databinding.DialogQrCodeBinding
import com.casecode.pos.viewmodel.ItemsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QRCodeDialogFragment : DialogFragment() {
    private var _binding: DialogQrCodeBinding? = null
    val binding: DialogQrCodeBinding
        get() = _binding!!
    private val viewModel: ItemsViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogQrCodeBinding.inflate(layoutInflater)

        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(binding.root).setTitle(getString(R.string.print_qr_code_title))
            .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                // Respond to negative button press
            }.setPositiveButton("Print") { dialog, which ->
                // Respond to positive button press
            }

        return builder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.itemSelected.observe(viewLifecycleOwner) {
            binding.item = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        fun newInstance(
        ): QRCodeDialogFragment {
            return QRCodeDialogFragment()
        }
    }
}