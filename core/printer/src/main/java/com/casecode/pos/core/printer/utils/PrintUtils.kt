/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.casecode.pos.core.printer.utils

import com.casecode.pos.core.model.data.users.Item
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PrintUtils {
    fun generatePrintText(invoiceId: String, phone: String, items: List<Item>): String {
        val format = SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss", Locale.getDefault())
        val currentDate = format.format(Date())
        buildString {
            items.forEach { item ->
                appendLine("[L]<b>${item.name}</b>[R]${item.unitPrice}€\n")
                appendLine("[L]  + qty : ${item.quantity}\n")
                appendLine("[L]\n")
            }
        }
        val itemsText = StringBuilder()
        val totalPrice = items.sumOf { it.unitPrice * it.quantity.toDouble() }

        items.forEach { item ->
            itemsText.append("[L]<b>${item.name}</b>[R]${item.unitPrice}€\n")
            itemsText.append("[L]  + qty : ${item.quantity}\n")
            itemsText.append("[L]\n")
        }

        return (
            "[L]\n" +
                "[C]<u><font size='big'>ORDER $invoiceId</font></u>\n" +
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

    fun generateBarcode(item: Item): String = (
        "[L]\n" +
            "[C]<u><font size='big'>${item.name}</font></u>\n" +
            "[L]<b>${item.name}</b>[R]${item.unitPrice}€\n" +
            "[L]  + qty : ${item.quantity}\n" +
            "[L]\n" +
            "[C]<qrcode size='20'>${item.sku}</qrcode>\n"
        )

    fun test(logo: String): String = (
        "[C]<img>" + logo + "</img>\n" +
            "[C]<u><font size='big-4'>POS</font></u>\n" +
            "[C]<u><font size='big-4'>نقطه بيع</font></u>\n" +
            "<u></u>" +
            "[C]<u><font size='big'>Item test1</font></u>\n" +
            "[L]<b>item 1</b>[R]20.00$\n" +
            "[L]  + qty : 5.0\n" +
            "[L]\n" +
            "[C]<qrcode size='20'> 2222112121</qrcode>\n"
        )

    fun testWithoutLogo(): String = (
        "[C]<u><font size='big-4'>POS</font></u>\n" +
            "[C]<u><font size='big-4'>نقطه بيع</font></u>\n" +
            "<u></u>" +
            "[C]<u><font size='big'>Item test1</font></u>\n" +
            "[L]<b>item 1</b>[R]20.00$\n" +
            "[L]  + qty : 5.0\n" +
            "[L]\n" +
            "[C]<qrcode size='20'> 2222112121</qrcode>\n"
        )
}
/**
 * EscPosPrinter printer = new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 48f, 32);
 * printer
 *     .printFormattedText(
 *         "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.getApplicationContext().getResources()
 *         .getDrawableForDensity(R.drawable.logo, DisplayMetrics.DENSITY_MEDIUM))+"</img>\n" +
 *         "[L]\n" +
 *         "[C]<u><font size='big'>ORDER N°045</font></u>\n" +
 *         "[L]\n" +
 *         "[C]================================\n" +
 *         "[L]\n" +
 *         "[L]<b>BEAUTIFUL SHIRT</b>[R]9.99e\n" +
 *         "[L]  + Size : S\n" +
 *         "[L]\n" +
 *         "[L]<b>AWESOME HAT</b>[R]24.99e\n" +
 *         "[L]  + Size : 57/58\n" +
 *         "[L]\n" +
 *         "[C]--------------------------------\n" +
 *         "[R]TOTAL PRICE :[R]34.98e\n" +
 *         "[R]TAX :[R]4.23e\n" +
 *         "[L]\n" +
 *         "[C]================================\n" +
 *         "[L]\n" +
 *         "[L]<font size='tall'>Customer :</font>\n" +
 *         "[L]Raymond DUPONT\n" +
 *         "[L]5 rue des girafes\n" +
 *         "[L]31547 PERPETES\n" +
 *         "[L]Tel : +33801201456\n" +
 *         "[L]\n" +
 *         "[C]<barcode type='ean13' height='10'>831254784551</barcode>\n" +
 *         "[C]<qrcode size='20'>https://dantsu.com/</qrcode>"
 *     );
 */