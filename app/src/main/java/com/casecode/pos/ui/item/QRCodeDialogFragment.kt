package com.casecode.pos.ui.item

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.casecode.data.utils.encodeAsBitmap
import com.casecode.pos.databinding.DialogQrCodeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class QRCodeDialogFragment : DialogFragment() {

    private var _binding: DialogQrCodeBinding? = null
    private val binding: DialogQrCodeBinding
        get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogQrCodeBinding.inflate(layoutInflater)
        val view = binding.root

        // Get barcode and name from arguments
        val barcode = arguments?.getString("barcode") ?: ""
        val name = arguments?.getString("name") ?: ""

        val qrCodeBitmap = barcode.encodeAsBitmap()

        binding.imageViewQrCode.setImageBitmap(qrCodeBitmap)
        binding.itemNameTextView.text = name
        binding.textViewBarcode.text = barcode

        val builder = MaterialAlertDialogBuilder(requireActivity())
        builder.setView(view)
            .setTitle("Print QR code")
            .setNegativeButton("Decline") { dialog, which ->
                // Respond to negative button press
            }
            .setPositiveButton("Print") { dialog, which ->
                // Respond to positive button press
            }

        return builder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}