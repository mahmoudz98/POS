/*
package com.casecode.pos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.casecode.domain.model.users.Invoice
import com.casecode.domain.model.users.InvoiceGroup
import com.casecode.pos.R
import com.casecode.pos.base.BaseAdapter
import com.casecode.pos.base.BaseViewHolder
import com.casecode.pos.databinding.ItemInvoiceBinding
import com.casecode.pos.databinding.ItemInvoicesGroupBinding
import timber.log.Timber
import java.util.Locale


class InvoiceGroupAdapter(private val onItemClick: (Invoice) -> Unit) :
    BaseAdapter<InvoiceGroup>(InvoiceDiffCallback), Filterable {
    private var originalItems: List<InvoiceGroup> = emptyList()

    companion object InvoiceDiffCallback : DiffUtil.ItemCallback<InvoiceGroup>() {
        override fun areItemsTheSame(
            oldInvoice: InvoiceGroup,
            newInvioce: InvoiceGroup,
        ): Boolean {
            return oldInvoice === newInvioce
        }
        override fun areContentsTheSame(
            oldInvoice: InvoiceGroup,
            newInvioce: InvoiceGroup,
        ): Boolean {
            return oldInvoice == newInvioce
        }
    }

    inner class InvoiceGroupViewMolder(binding: ItemInvoicesGroupBinding) :
        BaseViewHolder<ItemInvoicesGroupBinding, InvoiceGroup>(binding) {
        override fun bind(element: InvoiceGroup) {
            super.bind(element)
            val viewPool = RecycledViewPool()

            binding.invoices = element.invoices
            val adapter = InvoiceAdapter(onItemClick)
            binding.rvInvoices.adapter = adapter
            binding.rvInvoices.setRecycledViewPool(viewPool)
            binding.dateInvoiceGroup = element.date
            binding.totalGroup = element.totalInvoiceGroup.toString()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BaseViewHolder<out ItemInvoicesGroupBinding, InvoiceGroup> {
        return InvoiceGroupViewMolder(
            ItemInvoicesGroupBinding.inflate(
                LayoutInflater.from(
                    parent.context,
                ),
                parent, false,
            ),
        )
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_invoices_group
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    fun submitOriginalList(list: MutableList<InvoiceGroup>?) {
        originalItems = list ?: emptyList()
        if(currentList.isEmpty()) submitList(list)
      // clearFilter()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val queryString = constraint?.toString()
                return FilterResults().apply {
                    values = if (queryString.isNullOrBlank()) {
                        originalItems
                    } else {
                        onFilter(queryString)
                    }
                }
            }

            private fun onFilter(date: String): List<InvoiceGroup> {
                return originalItems.filter { invoiceGroup ->
                    invoiceGroup.date.contains(date)
                }
            }

            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults?,
            ) {
                @Suppress("UNCHECKED_CAST") val filteredList =
                    results?.values as? List<InvoiceGroup> ?: emptyList()
                submitList(filteredList.toMutableList())
            }
        }
    }
    fun filterInvoiceDate(query: String) {
        filter.filter(query)
    }
    fun clearFilter(){
        filter.filter("")
    }
}


class InvoiceAdapter(private val onItemClick: (Invoice) -> Unit) :
    BaseAdapter<Invoice>(InvoiceDiffCallback) {
    companion object InvoiceDiffCallback : DiffUtil.ItemCallback<Invoice>() {
        override fun areItemsTheSame(
            oldInvoice: Invoice,
            newInvioce: Invoice,
        ): Boolean {
            return oldInvoice === newInvioce
        }

        override fun areContentsTheSame(
            oldInvoice: Invoice,
            newInvioce: Invoice,
        ): Boolean {
            return oldInvoice == newInvioce
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_invoice
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BaseViewHolder<out ViewDataBinding, Invoice> {
        return InvoiceViewHolder(
            ItemInvoiceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false,
            ),
        )
    }

    inner class InvoiceViewHolder(binding: ItemInvoiceBinding) :
        BaseViewHolder<ItemInvoiceBinding, Invoice>(binding) {

        override fun bind(element: Invoice) {
            super.bind(element)
            binding.invoice = element
            binding.root.setOnClickListener {
                onItemClick(element)
            }
        }
    }
}*/