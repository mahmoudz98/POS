package com.casecode.pos.ui.pos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.casecode.domain.model.users.Item
import com.casecode.pos.R
import com.casecode.pos.adapter.SaleAdapter
import com.casecode.pos.adapter.SearchItemSaleAdapter
import com.casecode.pos.base.doAfterTextChangedListener
import com.casecode.pos.databinding.FragmentSaleBinding
import com.casecode.pos.utils.setupSnackbar
import com.casecode.pos.utils.showSnackbar
import com.casecode.pos.utils.startScanningBarcode
import com.casecode.pos.viewmodel.SaleViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class PointOfSaleFragment : Fragment() {
    private var _binding: FragmentSaleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SaleViewModel by viewModels()
    private var job :Job? = null

    private val barLauncher = registerForActivityResult(ScanContract()) { result ->
        result.contents.let {
            if (it == null) {
                binding.root.showSnackbar(
                    getString(R.string.message_scan_error),
                    Snackbar.LENGTH_SHORT,
                )
            } else {
                // Handle the scanned barcode result
                viewModel.scanItem(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSaleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this.viewLifecycleOwner
        setup()
    }

    private fun setup() {
        setupSaleAdapter()
        setupSearchItemAdapter()
        setupObserver()
        setupGetRestOfAmount()
        setupClicks()
    }

    private fun setupObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.isLoading = it
        }
        viewModel.isEmptyItems.observe(viewLifecycleOwner) {
            binding.isEmpty = it
        }
        viewModel.isInvoiceEmpty.observe(viewLifecycleOwner) {
            binding.isInvoiceEmpty = it
        }
        viewModel.itemsInvoice.observe(viewLifecycleOwner) {
            binding.items = it.toMutableList()
        }
        viewModel.items.observe(viewLifecycleOwner) {
            binding.actvSaleSearchItem.setAutoCompleteItems(it.toMutableList())
        }
        viewModel.totalItemsInvoice.observe(viewLifecycleOwner) {
            binding.totalInvoice = it
        }
        setupObserverSnackbar()

    }

    private fun setupObserverSnackbar() {
        binding.root.setupSnackbar(
            viewLifecycleOwner, viewModel.userMessage, Snackbar.LENGTH_SHORT,
            binding.tilSaleSearchItem,
        )
    }

    private fun setupClicks() {
        binding.imgBtnSaleScanner.setOnClickListener {
            barLauncher.launch(ScanOptions().startScanningBarcode(requireContext()))
        }
        binding.actvSaleSearchItem.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedItem = parent.getItemAtPosition(position) as Item
                viewModel.addItemInvoice(selectedItem)
                binding.tilSaleSearchItem.editText?.text?.clear()
            }
        binding.btnSale.setOnClickListener {
            viewModel.updateStockAndAddItemInvoice()
        }
    }
    private fun setupGetRestOfAmount(){
        binding.tilSaleAmountTotal.editText?.doAfterTextChangedListener {
            job?.cancel()
            job = lifecycleScope.launch {
                delay(500)
                if (it.isNullOrEmpty()) return@launch
                val amount = it.toString().trim().toDouble()
                val restOfAmount = viewModel.setRestOfAmount(amount)

                binding.tilSaleAmountTotal.helperText = null
                binding.tilSaleAmountTotal.helperText =
                    getString(R.string.text_sale_total_hint) + viewModel.totalItemsInvoice.value + getString(
                        R.string.text_sale_rest_of_amount,
                    ) + restOfAmount

            }
        }
    }
    private fun setupSearchItemAdapter() {
        val adapter = SearchItemSaleAdapter(requireContext(), mutableListOf())
        binding.actvSaleSearchItem.setAdapter(adapter)
    }

    private fun setupSaleAdapter() {
        val saleAdapter: SaleAdapter by lazy {
            SaleAdapter(
                onItemClick = { updateQuantityItem(it) },
                onItemLongClick = { deleteItem(it) },
            )
        }

        binding.rvSale.adapter = saleAdapter
    }

    private fun updateQuantityItem(item: Item) {
        viewModel.itemInvoiceSelected(item)
        QuantityItemInvoiceDialog.newInstance().show(
            childFragmentManager,
            QuantityItemInvoiceDialog.QUANTITY_ITEM_INVOICE_DIALOG,
        )

    }

    private fun deleteItem(item: Item) {
        val builder =
            MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.delete_item_invoice_title)
                .setMessage(R.string.delete_item_message)
                .setPositiveButton(R.string.delete) { _, _ ->
                    viewModel.deleteItemInvoice(item)
                }.setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
        builder.create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
        _binding = null
        viewModelStore.clear()
    }

}