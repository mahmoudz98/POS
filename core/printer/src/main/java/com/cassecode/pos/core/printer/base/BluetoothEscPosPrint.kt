package com.cassecode.pos.core.printer.base

import android.content.Context
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BluetoothEscPosPrint(
    val context: Context,
    onPrintFinished: OnPrintFinished? = null,
) : EscPosPrint(context, onPrintFinished) {

    override suspend fun printAsync(printerData: EscPosPrinterService): PrinterStatus =
        withContext(Dispatchers.IO) {
            val deviceConnection = printerData.getPrinterConnection()
            publishProgress(PROGRESS_CONNECTING)
            if (deviceConnection == null) {
                val newPrinterConnection = BluetoothPrintersConnections.selectFirstPaired()
                val updatedPrinterData = newPrinterConnection?.let {
                    EscPosPrinterService(
                        it,
                        printerData.printerDpi,
                        printerData.printerWidthMM,
                        printerData.printerNbrCharactersPerLine,
                    ).apply {
                        setTextsToPrint(printerData.getTextsToPrint())
                    }
                }

                // Try to connect to the new Bluetooth printer
                try {
                    newPrinterConnection?.connect()
                } catch (e: EscPosConnectionException) {
                    e.printStackTrace()
                    return@withContext PrinterStatus(
                        updatedPrinterData,
                        FINISH_PRINTER_DISCONNECTED,
                    )
                }

                // Continue with the updated printer data
                super.printAsync(updatedPrinterData!!)
            } else {
                printerData.getPrinterConnection()
                super.printAsync(printerData)
            }
        }

    override fun publishProgress(progress: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            dialog?.apply {
                when (progress) {
                    PROGRESS_CONNECTING -> setMessage("Connecting to Bluetooth printer...")
                    PROGRESS_CONNECTED -> setMessage("Bluetooth printer is connected...")
                    PROGRESS_PRINTING -> setMessage("Bluetooth printer is printing...")
                    PROGRESS_PRINTED -> setMessage("Bluetooth printer has finished...")
                }
                setProgress(progress)
                setMax(4)
            }
        }
    }




}