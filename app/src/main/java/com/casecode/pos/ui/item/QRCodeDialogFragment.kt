package com.casecode.pos.ui.item

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.casecode.domain.model.users.Item
import com.casecode.pos.databinding.DialogQrCodeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class QRCodeDialogFragment(private val item: Item) : DialogFragment() {

    private var _binding: DialogQrCodeBinding? = null
    private val binding: DialogQrCodeBinding
        get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogQrCodeBinding.inflate(layoutInflater)

        val builder = MaterialAlertDialogBuilder(requireActivity())
        builder.setView(binding.root)
            .setTitle("Print QR code")
            .setNegativeButton("Decline") { dialog, which ->
                // Respond to negative button press
            }
            .setPositiveButton("Print") { dialog, which ->
                // Respond to positive button press
            }

        return builder.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.item = item
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}