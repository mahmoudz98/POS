package com.cassecode.pos.core.printer

import android.content.Context
import com.casecode.pos.core.model.data.PrinterInfo
import com.cassecode.pos.core.printer.base.BluetoothEscPosPrint
import com.cassecode.pos.core.printer.base.EscPosPrinterService
import com.cassecode.pos.core.printer.base.OnPrintFinished
import com.cassecode.pos.core.printer.utils.PrintUtils
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import timber.log.Timber
import javax.inject.Inject

class BluetoothPrinterConnection @Inject constructor() : PrinterConnection {

    private var selectedDevice: BluetoothConnection? = null

    override fun print(context: Context, printerInfo: PrinterInfo, printContent: PrintContent) {

        BluetoothEscPosPrint(
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
        ).execute(getAsyncEscPosPrinter(selectedDevice!!, printContent))
    }


    private fun getAsyncEscPosPrinter(
        printerConnection: BluetoothConnection,
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

                is PrintContent.Test -> {
                    PrintUtils.test()
                }
            }

            addTextToPrint(textToPrint)
        }
    }
}