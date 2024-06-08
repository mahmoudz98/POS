/*
package com.casecode.pos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.casecode.domain.model.users.Item
import com.casecode.pos.R
import com.casecode.pos.base.BaseAdapter
import com.casecode.pos.base.BaseViewHolder
import com.casecode.pos.databinding.ItemSaleBinding
import timber.log.Timber

class SaleAdapter(
    private val onItemClick: (Item) -> Unit,
    private val onItemLongClick: (Item) -> Unit,
) : BaseAdapter<Item>(DiffCallback) {

    */
/**
     * Allows the RecyclerView to determine which items have changed when the [List] of [Item]
     * has been updated.
     *//*

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
            return oldItem.quantity == newItem.quantity
        }
    }

    inner class SaleViewHolder(binding: ItemSaleBinding) :
        BaseViewHolder<ItemSaleBinding, Item>(binding) {
        init {
            binding.apply {
                root.setOnClickListener {
                    onItemClick(item!!)
                }
                root.setOnLongClickListener {
                    handleItemLongClick(item!!)
                }
            }
        }

        override fun bind(element: Item) {
            super.bind(element)
            binding.item = element
        }
        private fun handleItemLongClick(item: Item): Boolean {
            // Invoke the long-click listener with the clicked item
            onItemLongClick.invoke(item)
            return true // Return true to consume the long-click event
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BaseViewHolder<out ViewDataBinding, Item> {
        return SaleViewHolder(
            ItemSaleBinding.inflate(LayoutInflater.from(parent.context), parent, false),
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
        holder.bind(currentList[position])
    }

    override fun submitList(list: MutableList<Item>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
}*/