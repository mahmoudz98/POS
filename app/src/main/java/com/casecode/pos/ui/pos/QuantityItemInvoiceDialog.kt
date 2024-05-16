package com.casecode.pos.ui.pos

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.casecode.pos.R
import com.casecode.pos.base.doAfterTextChangedListener
import com.casecode.pos.databinding.DialogQuantityItemInvoiceBinding
import com.casecode.pos.viewmodel.SaleViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuantityItemInvoiceDialog : DialogFragment() {
    private var _binding: DialogQuantityItemInvoiceBinding? = null
    val binding get() = _binding!!
    private val viewModel: SaleViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
    )

    /**
     * Creates the dialog with the dialog layout.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext())
        _binding = DialogQuantityItemInvoiceBinding.inflate(layoutInflater)
        return builder.setView(binding.root).create()
    }

    /**
     * Inflates the dialog layout.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this.viewLifecycleOwner
        setup()
    }

    private fun setup() {
        setupObserver()
        setupClick()
        setupTextListener()

    }

    private fun setupObserver() {
        viewModel.itemInvoiceSelected.observe(viewLifecycleOwner) {
            binding.quantityItem = it?.quantity
        }
    }

    private fun setupTextListener() {
        binding.tilQuantityItemInvoice.editText?.doAfterTextChangedListener {
            val textQuantity =
                binding.tilQuantityItemInvoice.editText?.text.toString().trim().toDouble()

            val (isItemInStock, quantityItemInvoiceInStock) = viewModel.isItemSelectedQuantityInStock(
                textQuantity,
            )
            binding.tilQuantityItemInvoice.error = null
            if (!isItemInStock) {
                val errorMessageQuantity =
                    getString(R.string.quantity_item_invoice_error) + quantityItemInvoiceInStock
                binding.tilQuantityItemInvoice.error = errorMessageQuantity
            }
            if (textQuantity <= 0) {
                binding.tilQuantityItemInvoice.error =
                    getString(R.string.quantity_item_invoice_error_zero)
            }

        }
    }

    private fun setupClick() {
        binding.btnQuantityItemInvoice.setOnClickListener {
            if (isQuantityItemInStockValid()) {
                val quantity =
                    binding.tilQuantityItemInvoice.editText?.text.toString().trim().toDouble()
                viewModel.updateQuantityItemInvoice(quantity)
                dismiss()
            }
        }
    }

    private fun isQuantityItemInStockValid(): Boolean {
        val textQuantity =
            binding.tilQuantityItemInvoice.editText?.text.toString().trim().toDouble()
        val (isItemInStock, quantityItemInvoiceInStock) = viewModel.isItemSelectedQuantityInStock(
            textQuantity,
        )
        if (!isItemInStock) {
            binding.tilQuantityItemInvoice.error =
                getString(R.string.quantity_item_invoice_error) + quantityItemInvoiceInStock
            return false
        }
        if (textQuantity <= 0) {
            binding.tilQuantityItemInvoice.error =
                getString(R.string.quantity_item_invoice_error_zero)
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        const val QUANTITY_ITEM_INVOICE_DIALOG = "QUANTITY_ITEM_INVOICE_DIALOG"
        fun newInstance(
        ): QuantityItemInvoiceDialog {
            return QuantityItemInvoiceDialog()
        }
    }
}