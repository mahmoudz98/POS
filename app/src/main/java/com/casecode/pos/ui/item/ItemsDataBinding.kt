package com.casecode.pos.ui.item

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.casecode.data.utils.encodeAsBitmap
import com.casecode.domain.model.users.Invoice
import com.casecode.domain.model.users.InvoiceGroup
import com.casecode.domain.model.users.Item
import com.casecode.pos.adapter.InvoiceAdapter
import com.casecode.pos.adapter.InvoiceGroupAdapter
import com.casecode.pos.adapter.ItemsAdapter
import com.casecode.pos.adapter.SaleAdapter
import com.google.zxing.WriterException
import timber.log.Timber
import java.text.DecimalFormat

@BindingAdapter("bindListItems")
fun RecyclerView.bindListItems(items: List<Item>?) {
    items?.let {
        (adapter as ItemsAdapter).submitOriginalList(items.toMutableList())
    }
}

@BindingAdapter("priceWithCurrency")
fun setPriceWithCurrency(
    textView: TextView,
    price: Double,
) {
    val formattedPrice = DecimalFormat("#,###.##").format(price) + " EGP"
    textView.text = formattedPrice
}

@BindingAdapter("textToBitmap")
fun textToBitmap(
    imageView: ImageView,
    text: String?,
) {
    try {
        text?.let {
            val bitmap = it.encodeAsBitmap()
            imageView.setImageBitmap(bitmap)
        }
    } catch (e: WriterException) {
        e.printStackTrace()
    }
}

@BindingAdapter("quantityText")
fun setQuantityText(
    textView: TextView,
    quantity: Double,
) {
    val formattedQuantity = "Qty. $quantity"
    textView.text = formattedQuantity
}