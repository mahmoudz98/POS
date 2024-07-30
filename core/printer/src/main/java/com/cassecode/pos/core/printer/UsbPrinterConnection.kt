package com.cassecode.pos.core.printer

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.casecode.pos.core.model.data.PrinterInfo
import com.cassecode.pos.core.printer.base.EscPosPrinterService
import com.cassecode.pos.core.printer.base.OnPrintFinished
import com.cassecode.pos.core.printer.base.UsbEscPosPrint
import com.cassecode.pos.core.printer.utils.PrintUtils
import com.dantsu.escposprinter.connection.usb.UsbConnection
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections
import timber.log.Timber
import javax.inject.Inject

const val ACTION_USB_PERMISSION = "com.casecode.pos.core.printer.USB_PERMISSION"
class UsbPrinterConnection @Inject constructor() : PrinterConnection {
    private var printerContext: PrintContent? = null
    override fun print(context: Context, printerInfo: PrinterInfo, printContent: PrintContent) {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val usbConnection = UsbPrintersConnections.selectFirstConnected(context)
        if (usbConnection != null && usbManager != null) {
            val permissionIntent = PendingIntent.getBroadcast(
                context,
                0,
                Intent(ACTION_USB_PERMISSION),
                PendingIntent.FLAG_IMMUTABLE,
            )
            context.registerReceiver(usbReceiver, IntentFilter(ACTION_USB_PERMISSION))
            this.printerContext = printContent
            usbManager.requestPermission(usbConnection.device, permissionIntent)
        } else {
            AlertDialog.Builder(context)
                .setTitle("USB Connection")
                .setMessage("No USB printer found.")
                .show()
        }
    }

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (ACTION_USB_PERMISSION == intent.action) {
                val usbManager: UsbManager =
                    context.getSystemService(Context.USB_SERVICE) as UsbManager
                val usbDevice: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    usbDevice?.let {
                        UsbEscPosPrint(
                            context,
                            object : OnPrintFinished {
                                override fun onError(
                                    asyncEscPosPrinterService: EscPosPrinterService?,
                                    codeException: Int,
                                ) {
                                    Timber.tag("Async.OnPrintFinished").e("An error occurred!")
                                }

                                override fun onSuccess(asyncEscPosPrinterService: EscPosPrinterService?) {
                                    Timber.tag("Async.OnPrintFinished").i("Print is finished!")
                                }
                            },
                        ).execute(
                            getAsyncEscPosPrinter(
                                UsbConnection(usbManager, it),
                                printContext = printerContext!!,
                            ),
                        )
                    }
                }
            }
        }
    }

    private fun getAsyncEscPosPrinter(
        printerConnection: UsbConnection,
        printContext: PrintContent,
    ): EscPosPrinterService {
        return EscPosPrinterService(printerConnection, 203, 48f, 32).apply {
            val textToPrint = when (printContext) {
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
                is PrintContent.Test ->{
                    PrintUtils.test()
                }
            }

            addTextToPrint(textToPrint)
        }
    }
}