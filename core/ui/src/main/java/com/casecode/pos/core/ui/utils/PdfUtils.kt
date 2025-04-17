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
package com.casecode.pos.core.ui.utils

import android.Manifest.permission
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import com.casecode.pos.core.model.data.users.SupplierInvoice
import com.casecode.pos.core.model.utils.toBigDecimalFormatted
import com.casecode.pos.core.model.utils.toFormattedDateTimeString
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import timber.log.Timber
import java.io.File
import java.io.IOException
import kotlin.math.abs

object PdfInvoiceUtils {

    private const val PDF_DIRECTORY = "Pos/Bills"
    private const val TITLE_FONT_SIZE = 24f
    private const val NUM_COLUMN_ITEM= 5
    private const val DOCUMENT_MARGIN = 50f
    private const val TITLE_BOTTOM_MARGIN = 30f
    private const val BOTTOM_MARGIN = 12f
    private const val TABLE_WIDTH_PERCENT = 100f
    private const val SUMMARY_TABLE_WIDTH_PERCENT = 50f

    fun createInvoicePDF(context: Context, supplierInvoice: SupplierInvoice):File {

            val file = File(context.getExternalFilesDir(null), "${supplierInvoice.billNumber}.pdf")
            PdfWriter(file).use { writer ->
                com.itextpdf.kernel.pdf.PdfDocument(writer).use { pdfDocument ->
                    Document(pdfDocument, PageSize.A4).apply {
                        setMargins(
                            DOCUMENT_MARGIN,
                            DOCUMENT_MARGIN,
                            DOCUMENT_MARGIN,
                            DOCUMENT_MARGIN,
                        )
                        addTitle()
                        addBillFromDetails(supplierInvoice)
                        addItemsTable(supplierInvoice)
                        addSummaryTable(supplierInvoice)
                        close()
                    }
                }
            }
        return file

    }

    private fun Document.addTitle() {
        add(
            Paragraph("Bill")
                .setFontSize(TITLE_FONT_SIZE)
                .setFontColor(ColorConstants.BLACK)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(TITLE_BOTTOM_MARGIN),
        )
    }

    private fun Document.addBillFromDetails(supplierInvoice: SupplierInvoice) {
        val table = Table(1).apply {
            width = com.itextpdf.layout.properties.UnitValue.createPercentValue(TABLE_WIDTH_PERCENT)
        }

        with(supplierInvoice) {
            table.addCell(createNoBorderCell("Bill From: $supplierName"))
            table.addCell(createNoBorderCell("Bill Date: ${issueDate.toFormattedDateTimeString()}"))
            table.addCell(createNoBorderCell("Due Date: ${dueDate.toFormattedDateTimeString()}"))
        }
        table.setMarginBottom(BOTTOM_MARGIN)

        add(table)
    }

    private fun Document.addItemsTable(supplierInvoice: SupplierInvoice) {
        val table = Table(NUM_COLUMN_ITEM).apply {
            width = com.itextpdf.layout.properties.UnitValue.createPercentValue(TABLE_WIDTH_PERCENT)
            setHorizontalAlignment(HorizontalAlignment.CENTER)
        }

        addTableHeaderCell(table, "#")
        addTableHeaderCell(table, "Item")
        addTableHeaderCell(table, "Qty")
        addTableHeaderCell(table, "Cost Price")
        addTableHeaderCell(table, "Amount")

        supplierInvoice.invoiceItems.forEachIndexed {index,  item ->
            table.addCell("${index+1}")
            table.addCell(item.name)
            table.addCell(item.quantity.toString())
            table.addCell(item.costPrice.toBigDecimalFormatted())
            table.addCell(item.costPrice.times(item.quantity).toBigDecimalFormatted())
        }
        add(table)
    }

    private fun Document.addSummaryTable(supplierInvoice: SupplierInvoice) {
        val table = Table(2).apply {
            width = com.itextpdf.layout.properties.UnitValue.createPercentValue(
                SUMMARY_TABLE_WIDTH_PERCENT,
            )
            setHorizontalAlignment(HorizontalAlignment.RIGHT)
        }

        with(supplierInvoice) {
            table.addCell(createNoBorderCell("Sub Total:"))
            table.addCell(createNoBorderCell(subTotal.toBigDecimalFormatted()))
            if(amountDiscounted != 0.0) {
                table.addCell(createNoBorderCell("Adjustment:"))
                table.addCell(createNoBorderCell(amountDiscounted.toBigDecimalFormatted()))
            }
            table.addCell(createNoBorderCell("Total:"))
            table.addCell(createNoBorderCell(totalAmount.toBigDecimalFormatted()))

            table.addCell(createNoBorderCell("Payments Made:"))
            table.addCell(createNoBorderCell(restDueAmount.toBigDecimalFormatted()))

            table.addCell(createBorderCell("Balance Due:"))
            table.addCell(createBorderCell(abs(totalAmount - restDueAmount).toBigDecimalFormatted()))
        }
        table.setMarginTop(BOTTOM_MARGIN)
        add(table)
    }

    private fun createNoBorderCell(text: String): Cell {
        return Cell().add(Paragraph(text)).setBorder(Border.NO_BORDER)
    }
    private fun createBorderCell(text:String):Cell{
        return Cell().add(Paragraph(text)).setBackgroundColor(ColorConstants.LIGHT_GRAY,0.5f)
            .setBorder(Border.NO_BORDER)
    }

    fun addTableHeaderCell(table: Table, text: String) {
        table.addHeaderCell(
            Cell().add(Paragraph(text))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.LEFT),
        )
    }

    fun savePDFUsingFileProvider(context: Context, file: File): Uri {
        if (checkSelfPermission(context, permission.POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
            return Uri.EMPTY
        }
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val appDir = File(documentsDir, PDF_DIRECTORY)
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        val destinationFile = File(appDir, file.name)
        file.copyTo(destinationFile, overwrite = true)

        val authority = "${context.packageName}.fileprovider"
        Timber.e("authority: $authority")
        return FileProvider.getUriForFile(context, authority, destinationFile)
    }

    fun openPDF(context: Context, fileUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(fileUri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Check if there's an app that can handle the intent
        val packageManager = context.packageManager
        if (intent.resolveActivity(packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "No PDF viewer app found", Toast.LENGTH_SHORT).show()
        }
    }
}