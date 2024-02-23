package com.casecode.pos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import coil.load
import com.casecode.domain.model.users.Item
import com.casecode.pos.R
import com.casecode.pos.base.BaseAdapter
import com.casecode.pos.base.BaseViewHolder
import com.casecode.pos.databinding.ItemItemBinding

class ItemAdapter(
    val itemClick: (Item) -> Unit,
    val itemLongClick: (Item) -> Unit,
) : BaseAdapter<Item>(DiffCallback) {

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [Item]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }

    inner class ItemViewHolder(binding: ItemItemBinding) :
        BaseViewHolder<ItemItemBinding, Item>(binding) {

        init {
            binding.root.setOnClickListener { binding.item?.let { itemClick(it) } }

            // Handle long-click events
            binding.root.setOnLongClickListener {
                binding.item?.let { item -> onItemLongClicked(item) }
                true // Return true to consume the long-click event
            }
        }

        override fun bind(element: Item) {
            super.bind(element)

            binding.item = element

            binding.imvItem.load(element.imageUrl) {
                placeholder(R.drawable.outline_image_24)
                error(R.drawable.outline_hide_image_24)
            }

            // Bind other data to TextViews
            binding.textName.text = element.name
            binding.textQuantity.text = context.getString(R.string.qty, element.quantity.toString())
            binding.textPrice.text = context.getString(R.string.egp, element.price.toString())
            // Bind other data as needed
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BaseViewHolder<out ViewDataBinding, Item> {
        return ItemViewHolder(
            ItemItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_item
    }

    /**
     *  parent list is immutable and override here to use mutableList.
     */
    override fun submitList(list: MutableList<Item>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    // Define a function to handle long-click events
    private fun onItemLongClicked(item: Item): Boolean {
        // Invoke the long-click listener with the clicked item
        itemLongClick.invoke(item)
        return true // Return true to consume the long-click event
    }
}