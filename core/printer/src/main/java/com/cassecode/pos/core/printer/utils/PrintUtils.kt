package com.cassecode.pos.core.printer.utils

import com.casecode.pos.core.model.data.users.Item
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PrintUtils {

    fun generatePrintText(invoiceId : String, phone:String, items: List<Item>): String {
        val format = SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss", Locale.getDefault())
        val currentDate = format.format(Date())

        val itemsText = StringBuilder()
        val totalPrice = items.sumOf { it.price * it.quantity }

        items.forEach { item ->
            itemsText.append("[L]<b>${item.name}</b>[R]${item.price}€\n")
            itemsText.append("[L]  + qty : ${item.quantity}\n")
            itemsText.append("[L]\n")
        }

        return (
                "[L]\n" +
                        "[C]<u><font size='big'>ORDER ${invoiceId}</font></u>\n" +
                        "[L]\n" +
                        "[C]<u type='double'>$currentDate</u>\n" +
                        "[C]\n" +
                        "[C]================================\n" +
                        "[L]\n" +
                        itemsText.toString() +
                        "[C]--------------------------------\n" +
                        "[R]TOTAL PRICE :[R]${"%.2f".format(totalPrice)}€\n" +
                        "[L]\n" +
                        "[C]================================\n" +
                        "[L]\n" +
                        "[L]Tel : ${phone}\n"
                )
    }

    fun generateBarcode(item: Item): String {
        return (
                "[L]\n" +
                        "[C]<u><font size='big'>${item.name}</font></u>\n" +
                        "[L]<b>${item.name}</b>[R]${item.price}€\n" +
                        "[L]  + qty : ${item.quantity}\n" +
                        "[L]\n" +
                        "[C]<qrcode size='20'>${item.sku}</qrcode>\n"
                )
    }
    fun test(): String {
        return (
                "[L]\n" +
                        "[C]<u><font size='big'>Item 1</font></u>\n" +
                        "[L]<b>item 1</b>[R]20.00$\n" +
                        "[L]  + qty : 5.0\n" +
                        "[L]\n" +
                        "[C]<qrcode size='20'> 2222112121</qrcode>\n"
                )
    }
}