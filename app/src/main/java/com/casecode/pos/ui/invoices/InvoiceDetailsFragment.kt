/*
package com.casecode.pos.ui.invoices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.casecode.pos.R
import com.casecode.pos.adapter.SaleAdapter
import com.casecode.pos.databinding.FragmentInvoiceDetailsBinding
import com.casecode.pos.viewmodel.InvoicesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InvoiceDetailsFragment : Fragment() {

    private var _binding: FragmentInvoiceDetailsBinding? = null
    val binding get() = _binding!!
    private val invoicesViewModel: InvoicesViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentInvoiceDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        setupObserver()
        setupMenu()
        setupItemAdapter()
    }

    private fun setupObserver() {
        invoicesViewModel.isLoading.observe(viewLifecycleOwner) {
            binding.isLoading = it
        }
        invoicesViewModel.invoiceSelected.observe(viewLifecycleOwner) {
            binding.invoice = it
        }
    }

    private fun setupMenu() {
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.removeItem(R.id.action_main_profile)
                menuInflater.inflate(R.menu.menu_invoice_details_fragment, menu)

                menu.findItem(R.id.action_invoice_details_print).setOnMenuItemClickListener {
                    // TODO: handle print invoice.
                    true
                }

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_invoice_details_print -> true
                    else -> false
                }
            }
        }

        requireActivity().addMenuProvider(
            menuProvider, viewLifecycleOwner,
            Lifecycle.State.RESUMED,
        )
    }

    private fun setupItemAdapter() {
        val saleAdapter: SaleAdapter by lazy {
            SaleAdapter(onItemClick = {}, onItemLongClick = {})
        }

        binding.rvInvoiceDetails.adapter = saleAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}*/