/*
package com.casecode.pos.ui.pos

import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.recyclerview.widget.RecyclerView
import com.casecode.domain.model.users.Item
import com.casecode.pos.adapter.SaleAdapter
import com.casecode.pos.adapter.SearchItemSaleAdapter
import timber.log.Timber

//@BindingAdapter("bindSearchItems")
fun AppCompatAutoCompleteTextView.setAutoCompleteItems(items: MutableList<Item>?) {
    Timber.e("searchItems $items")
    if (items != null) {
        (adapter as SearchItemSaleAdapter).updateItems(items)
    }
}

//@BindingAdapter("bindListInvoiceItems")
fun RecyclerView.bindListInvoiceItems(items: MutableList<Item>?) {
    Timber.i("size of bindListInvoiceItems = ${items?.size}")
    Timber.e(" List of bindListInvoiceItems = $items")

    items?.let {
        (adapter as SaleAdapter).submitList(items)
    }
}*/