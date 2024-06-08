/*
package com.casecode.pos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.casecode.domain.model.users.Item
import com.casecode.pos.R
import com.casecode.pos.databinding.ItemSearchItemSaleBinding
import com.casecode.pos.utils.showSnackbar
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class SearchItemSaleAdapter(context: Context, private var items: MutableList<Item>) :
    ArrayAdapter<Item>(context, R.layout.item_search_item_sale, items) {
    private val filteredItemList = mutableListOf<Item>()

    private var selectedItemPosition = -1

    fun setSelectedItem(position: Int) {
        selectedItemPosition = position
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemSearchItemSaleBinding
        val view: View
        if (convertView == null) {
            val layoutInflater = LayoutInflater.from(context)
            binding = ItemSearchItemSaleBinding.inflate(layoutInflater, parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as ItemSearchItemSaleBinding
            view = convertView
        }
        try {
            val item = getItem(position)
            binding.item = item
        } catch (e: IndexOutOfBoundsException) {
            binding.root.showSnackbar(
                binding.root.context.getString(R.string.all_error_unknown),
                Snackbar.LENGTH_SHORT,
            )
        }

        return view
    }

    override fun getCount(): Int {
        return filteredItemList.size
    }

    override fun getItem(position: Int): Item {
        return filteredItemList[position]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint.isNullOrBlank()) {
                    filteredItemList.clear()
                } else {
                    val searchQuery = constraint.toString().lowercase(Locale.getDefault())
                    val filteredList = items.filter {
                        it.name.lowercase(Locale.getDefault())
                            .contains(searchQuery) || it.sku.lowercase(Locale.getDefault())
                            .contains(searchQuery)
                    }
                    filteredItemList.clear()
                    filteredItemList.addAll(filteredList)
                }
                filterResults.values = filteredItemList
                filterResults.count = filteredItemList.size
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }

    fun updateItems(newItems: List<Item>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}*/