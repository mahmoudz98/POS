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
import androidx.navigation.fragment.findNavController
import com.casecode.data.utils.toDateFormatString
import com.casecode.pos.R
import com.casecode.pos.adapter.InvoiceGroupAdapter
import com.casecode.pos.databinding.FragmentInvoicesBinding
import com.casecode.pos.viewmodel.InvoicesViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class InvoicesFragment : Fragment() {

    private var _binding: FragmentInvoicesBinding? = null
    val binding get() = _binding!!
    private val adapter: InvoiceGroupAdapter = InvoiceGroupAdapter(
        onItemClick = {
            invoicesViewModel.setSelectedInvoice(it)
            this.findNavController().navigate(R.id.action_nav_invoices_to_invoiceDetailsFragment)
        },
    )
    private val invoicesViewModel: InvoicesViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentInvoicesBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        setup()
    }

    private fun setup() {
        setupMenu()
        setupAdapter()
        setupObserver()
    }

    private fun setupMenu() {
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.removeItem(R.id.action_main_profile)
                menuInflater.inflate(R.menu.menu_invoices_fragment, menu)
                invoicesViewModel.hasDateInvoiceFilter.observe(viewLifecycleOwner){
                    menu.findItem(R.id.action_invoice_clear_date).isVisible = it ?: false
                }
                menu.findItem(R.id.action_invoice_date).setOnMenuItemClickListener {
                    showDatePicker()
                    true
                }
                menu.findItem(R.id.action_invoice_clear_date).setOnMenuItemClickListener {
                    adapter.clearFilter()
                    invoicesViewModel.setDateInvoiceSelected(null)
                    true
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_invoice_date -> true
                    R.id.action_invoice_clear_date ->   true
                    else ->  false
                }
            }
        }

        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner,
            Lifecycle.State.RESUMED)
    }

    private fun showDatePicker() {
        val dateSelected = invoicesViewModel.dateInvoiceSelected.value?: MaterialDatePicker.todayInUtcMilliseconds()
        val datePicker = MaterialDatePicker.Builder.datePicker().setSelection(
            dateSelected,
        ).build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            invoicesViewModel.setDateInvoiceSelected(selection)
        }
        datePicker.show(childFragmentManager, datePicker.toString())

    }
    private fun setupAdapter() {
        binding.rvInvoices.adapter = adapter
    }

    private fun setupObserver() {
        invoicesViewModel.invoices.observe(viewLifecycleOwner) {
            binding.invoices = it
        }
        invoicesViewModel.isLoading.observe(viewLifecycleOwner) {
            binding.isLoading = it
        }
        invoicesViewModel.isInvoiceEmpty.observe(viewLifecycleOwner) {
            binding.isEmpty = it
        }
        invoicesViewModel.dateInvoiceSelected.observe(viewLifecycleOwner){dateFilter->
            if(dateFilter!= null) {
                val date = Date(dateFilter)
                val dateFormat = date.toDateFormatString()
                adapter.filterInvoiceDate(dateFormat)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        viewModelStore.clear()
    }

}*/