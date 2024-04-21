package com.casecode.pos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.casecode.domain.model.users.Item
import com.casecode.pos.R
import com.casecode.pos.base.BaseAdapter
import com.casecode.pos.base.BaseViewHolder
import com.casecode.pos.databinding.ItemItemBinding
import java.util.Locale

class ItemsAdapter(
    private val onItemClick: (Item) -> Unit,
    private val onItemLongClick: (Item) -> Unit,
    private val onPrintButtonClick: (Item) -> Unit,
) : BaseAdapter<Item>(DiffCallback), Filterable {
    private var originalItems: List<Item> = emptyList()

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [Item]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(
            oldItem: Item,
            newItem: Item,
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: Item,
            newItem: Item,
        ): Boolean {
            return oldItem == newItem
        }

    }

    inner class ItemViewHolder(binding: ItemItemBinding) :
        BaseViewHolder<ItemItemBinding, Item>(binding) {
        init {
            binding.apply {
                root.setOnClickListener {
                    onItemClick(item!!)
                }
                root.setOnLongClickListener {
                    handleItemLongClick(item!!)
                }
                imageButtonPrintQrCode.setOnClickListener {
                    onPrintButtonClick(item!!)
                }
            }

        }

        override fun bind(element: Item) {
            super.bind(element)
            binding.item = element
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BaseViewHolder<out ViewDataBinding, Item> {
        return ItemViewHolder(
            ItemItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }
    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_item
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<out ViewDataBinding, Item>,
        position: Int,
    ) {
        if (holder is ItemViewHolder) {
            holder.bind(currentList[position])
        }
    }

    // Define a function to handle long-click events
    private fun handleItemLongClick(item: Item): Boolean {
        // Invoke the long-click listener with the clicked item
        onItemLongClick.invoke(item)
        return true // Return true to consume the long-click event
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val queryString = constraint?.toString()?.lowercase(Locale.getDefault())
                return FilterResults().apply {
                    if (queryString.isNullOrBlank()) {
                        values = originalItems
                    } else {
                        values = onFilter(queryString)
                    }
                }
            }

            private fun onFilter(queryString: String): List<Item> {
                return originalItems.filter { item ->
                    item.name.lowercase(Locale.getDefault())
                        .contains(queryString) || item.sku.contains(queryString)
                }
            }

            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults?,
            ) {
                @Suppress("UNCHECKED_CAST") val filteredList =
                    results?.values as? List<Item> ?: emptyList()
                submitList(filteredList.toMutableList())
            }
        }
    }

    fun filterItems(query: String) {
        filter.filter(query)
    }

     fun submitOriginalList(list: MutableList<Item>?) {
         originalItems = list ?: emptyList()
        filterItems("")

    }
}