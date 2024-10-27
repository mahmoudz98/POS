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
package com.casecode.pos.core.printer.base

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.DisplayMetrics
import com.casecode.pos.core.model.data.PrinterInfo
import com.casecode.pos.core.printer.R
import com.casecode.pos.core.printer.model.PrintContent
import com.casecode.pos.core.printer.utils.PrintUtils
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.usb.UsbConnection
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import javax.inject.Inject

@Suppress("DEPRECATION")
class UsbEscPosPrint
@Inject
constructor() : EscPosPrint() {
    companion object {
        const val ACTION_USB_PERMISSION = "com.casecode.pos.core.printer.USB_PERMISSION"
    }

    private var printerContext: PrintContent? = null

    override fun print(
        context: Context,
        printerInfo: PrinterInfo,
        printContent: PrintContent,
    ) {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val usbConnection = UsbPrintersConnections.selectFirstConnected(context)

        if (usbConnection != null) {
            val permissionIntent =
                PendingIntent.getBroadcast(
                    context,
                    0,
                    Intent(ACTION_USB_PERMISSION),
                    PendingIntent.FLAG_IMMUTABLE,
                )
            context.registerReceiver(
                usbReceiver,
                IntentFilter(ACTION_USB_PERMISSION),
            )
            this.printerContext = printContent
            usbManager.requestPermission(usbConnection.device, permissionIntent)
        }
    }

    private val usbReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent,
            ) {
                if (ACTION_USB_PERMISSION == intent.action) {
                    val usbManager: UsbManager =
                        context.getSystemService(Context.USB_SERVICE) as UsbManager
                    val usbDevice: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        usbDevice?.let {
                            execute(
                                getEscPosPrinterService(
                                    UsbConnection(usbManager, it),
                                    printContext = printerContext!!,
                                    48f, // TODO: handle paper size
                                    context,
                                ),
                            )
                        }
                    }
                }
            }
        }

    override fun <T : DeviceConnection> getEscPosPrinterService(
        printerConnection: T,
        printContext: PrintContent,
        widthPaper: Float,
        context: Context,
    ): EscPosPrinter =
        EscPosPrinter(printerConnection, 203, widthPaper, 32).apply {
            val textToPrint =
                when (printContext) {
                    is PrintContent.Receipt -> {
                        PrintUtils.generatePrintText(
                            printContext.invoiceId,
                            printContext.phone,
                            printContext.items,
                        )
                    }

                    is PrintContent.QrCode -> {
                        PrintUtils.generateBarcode(printContext.item)
                    }

                    is PrintContent.Test -> {
                        val logo =
                            PrinterTextParserImg.bitmapToHexadecimalString(
                                this,
                                context.resources.getDrawableForDensity(
                                    R.drawable.core_printer_ic_point_of_sale_24,
                                    DisplayMetrics.DENSITY_MEDIUM,
                                    null,
                                ),
                            )
                        PrintUtils.test(logo)
                    }
                }

            addTextToPrint(textToPrint)
        }
}