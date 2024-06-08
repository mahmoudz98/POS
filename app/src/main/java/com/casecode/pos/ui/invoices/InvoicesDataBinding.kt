/*
package com.casecode.pos.ui.invoices

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.casecode.data.utils.toDateFormatString
import com.casecode.domain.model.users.Invoice
import com.casecode.domain.model.users.InvoiceGroup
import com.casecode.pos.adapter.InvoiceAdapter
import com.casecode.pos.adapter.InvoiceGroupAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//@BindingAdapter("bindListInvoices")
fun RecyclerView.bindListInvoices(items: List<Invoice>?) {
    items?.let {
        (adapter as InvoiceAdapter).submitList(items.toMutableList())
    }
}

//@BindingAdapter("bindListInvoicesGroup")
fun RecyclerView.bindListInvoicesGroup(items: List<InvoiceGroup>?) {
    items?.let {
        (adapter as InvoiceGroupAdapter).submitOriginalList(items.toMutableList())
    }
}

//@BindingAdapter("formattedDate")
fun setFormatedDate(
    textView: TextView,
    data: Date?,
) {
    data?.let {
        textView.text = it.toDateFormatString()
    }
}//@BindingAdapter("formattedDateTime")

fun setFormatedDateTime(
    textView: TextView,
    data: Date?,
) {
    data?.let {
        textView.text = it.toFormattedDateTimeString()
    }
}

fun Date.toFormattedDateTimeString(): String? {
    return this.let {

        SimpleDateFormat("MMM dd, yyyy hh:mm a ", Locale.getDefault()).format(this)
    }
}

//@BindingAdapter("formattedTime")
fun setFormattedTime(textView: TextView, dateTime: Date?) {
    dateTime?.let {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedTime = timeFormat.format(dateTime)
        textView.text = formattedTime
    }
}*/