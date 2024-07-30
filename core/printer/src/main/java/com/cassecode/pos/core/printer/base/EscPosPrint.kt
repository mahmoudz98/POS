package com.cassecode.pos.core.printer.base

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import com.dantsu.escposprinter.EscPosCharsetEncoding
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.exceptions.EscPosEncodingException
import com.dantsu.escposprinter.exceptions.EscPosParserException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

open class EscPosPrint(
    context: Context,
    private val onPrintFinished: OnPrintFinished? = null,
) {
    companion object {
        const val FINISH_SUCCESS = 1
        const val FINISH_NO_PRINTER = 2
        const val FINISH_PRINTER_DISCONNECTED = 3
        const val FINISH_PARSER_ERROR = 4
        const val FINISH_ENCODING_ERROR = 5
        const val FINISH_BARCODE_ERROR = 6
        const val PROGRESS_CONNECTING = 1
         const val PROGRESS_CONNECTED = 2
         const val PROGRESS_PRINTING = 3
         const val PROGRESS_PRINTED = 4
    }

    private val weakContext: WeakReference<Context> = WeakReference(context)
    var dialog: ProgressDialog? = null

    open suspend fun printAsync(printerData: EscPosPrinterService): PrinterStatus =
        withContext(Dispatchers.IO) {
            publishProgress(PROGRESS_CONNECTING)

            try {
                val deviceConnection: DeviceConnection? = printerData.getPrinterConnection()
                if (deviceConnection == null) {
                    return@withContext PrinterStatus(null, FINISH_NO_PRINTER)
                }

                val printer = EscPosPrinter(
                    deviceConnection,
                    printerData.printerDpi,
                    printerData.printerWidthMM,
                    printerData.printerNbrCharactersPerLine,
                    EscPosCharsetEncoding("windows-1252", 16),
                )

                publishProgress(PROGRESS_PRINTING)

                val textsToPrint = printerData.getTextsToPrint()
                textsToPrint.forEach { textToPrint ->
                    printer.printFormattedTextAndCut(textToPrint)
                    delay(500) // Coroutine-friendly delay
                }

                publishProgress(PROGRESS_PRINTED)
                PrinterStatus(printerData, FINISH_SUCCESS)
            } catch (e: EscPosConnectionException) {
                e.printStackTrace()
                PrinterStatus(printerData, FINISH_PRINTER_DISCONNECTED)
            } catch (e: EscPosParserException) {
                e.printStackTrace()
                PrinterStatus(printerData, FINISH_PARSER_ERROR)
            } catch (e: EscPosEncodingException) {
                e.printStackTrace()
                PrinterStatus(printerData, FINISH_ENCODING_ERROR)
            } catch (e: EscPosBarcodeException) {
                e.printStackTrace()
                PrinterStatus(printerData, FINISH_BARCODE_ERROR)
            } catch (e: Exception) {
                e.printStackTrace()
                PrinterStatus(printerData, FINISH_NO_PRINTER)
            }
        }

    fun execute(printerData: EscPosPrinterService) {
        val context = weakContext.get() ?: return

        showProgressDialog(context)

        CoroutineScope(Dispatchers.Main).launch {
            val result = printAsync(printerData)
            handleResult(context, result)
        }
    }

    private fun showProgressDialog(context: Context) {
        if (dialog == null) {
            dialog = ProgressDialog(context).apply {
                setTitle("Printing in progress...")
                setMessage("...")
                setProgressNumberFormat("%1d / %2d")
                setCancelable(false)
                setIndeterminate(false)
                setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                show()
            }
        }
    }

    open fun publishProgress(progress: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            dialog?.apply {
                when (progress) {
                    PROGRESS_CONNECTING -> setMessage("Connecting printer...")
                    PROGRESS_CONNECTED -> setMessage("Printer is connected...")
                    PROGRESS_PRINTING -> setMessage("Printer is printing...")
                    PROGRESS_PRINTED -> setMessage("Printer has finished...")
                }
                setProgress(progress)
                setMax(4)
            }
        }
    }

    private fun handleResult(context: Context, result: PrinterStatus) {
        dialog?.dismiss()
        dialog = null

        val message = when (result.printerStatus) {
            FINISH_SUCCESS -> "Congratulation ! The texts are printed !"
            FINISH_NO_PRINTER -> "The application can't find any printer connected."
            FINISH_PRINTER_DISCONNECTED -> "Unable to connect the printer."
            FINISH_PARSER_ERROR -> "It seems to be an invalid syntax problem."
            FINISH_ENCODING_ERROR -> "The selected encoding character returning an error."
            FINISH_BARCODE_ERROR -> "Data sent to be converted to barcode or QR code seems to be invalid."
            else -> "Unknown error"
        }

        AlertDialog.Builder(context)
            .setTitle("Printer Status")
            .setMessage(message)
            .show()

        when (result.printerStatus) {
            FINISH_SUCCESS -> onPrintFinished?.onSuccess(result.asyncEscPosPrinterService)
            else -> onPrintFinished?.onError(result.asyncEscPosPrinterService, result.printerStatus)
        }
    }
}