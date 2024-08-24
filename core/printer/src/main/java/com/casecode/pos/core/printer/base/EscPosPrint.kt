package com.casecode.pos.core.printer.base

import android.annotation.SuppressLint
import android.content.Context
import com.casecode.pos.core.data.service.LogService
import com.casecode.pos.core.model.data.PrinterInfo
import com.casecode.pos.core.printer.model.PrintContent
import com.casecode.pos.core.printer.model.PrinterStatus
import com.casecode.pos.core.printer.model.PrinterStatusCode
import com.dantsu.escposprinter.EscPosCharsetEncoding
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
import timber.log.Timber
import javax.inject.Inject
import com.dantsu.escposprinter.EscPosPrinter as EscPosPrinterLib

abstract class EscPosPrint {
    @Inject
    lateinit var printerStateManager: PrinterStateManager

    @Inject
    lateinit var logService: LogService

    abstract fun print(
        context: Context,
        printerInfo: PrinterInfo,
        printContent: PrintContent,
    )

    @SuppressLint("MissingPermission")
    open suspend fun takePrints(printerData: EscPosPrinter): PrinterStatus =
        withContext(Dispatchers.IO) {
            printerStateManager.publishState(PrinterStatusCode.PROGRESS_CONNECTING)
            val deviceConnection: DeviceConnection? = printerData.getPrinterConnection()

            try {
                val printer =
                    EscPosPrinterLib(
                        deviceConnection,
                        printerData.printerDpi,
                        printerData.printerWidthMM,
                        printerData.printerNbrCharactersPerLine,
                        EscPosCharsetEncoding("windows-1252", 16),
                    )
                //  printer.useEscAsteriskCommand(true)

                printerStateManager.publishState(PrinterStatusCode.PROGRESS_PRINTING)

                val textsToPrint = printerData.getTextsToPrint()
                Timber.e("textsToPrint: $textsToPrint")
                textsToPrint.forEach { textToPrint ->
                    Timber.e("textToPrint:foreach: $textToPrint")
                    printer.printFormattedTextAndCut(textToPrint)
                    delay(500)
                }

                printerStateManager.publishState(PrinterStatusCode.PROGRESS_PRINTED)
                PrinterStatus(printerData, PrinterStatusCode.FINISH_SUCCESS)
            } catch (e: Exception) {
                logService.logNonFatalCrash(e)
                handleException(e, printerData)
            }
        }

    private fun handleException(
        e: Exception,
        printerData: EscPosPrinter,
    ): PrinterStatus {
        e.printStackTrace()
        return when (e) {
            is EscPosConnectionException ->
                PrinterStatus(
                    printerData,
                    PrinterStatusCode.FINISH_PRINTER_DISCONNECTED,
                )

            is EscPosParserException ->
                PrinterStatus(
                    printerData,
                    PrinterStatusCode.FINISH_PARSER_ERROR,
                )

            is EscPosEncodingException ->
                PrinterStatus(
                    printerData,
                    PrinterStatusCode.FINISH_ENCODING_ERROR,
                )

            is EscPosBarcodeException ->
                PrinterStatus(
                    printerData,
                    PrinterStatusCode.FINISH_BARCODE_ERROR,
                )

            else -> PrinterStatus(printerData, PrinterStatusCode.FINISH_NO_PRINTER)
        }
    }

    open fun execute(printerData: EscPosPrinter) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = takePrints(printerData)
            printerStateManager.handleResult(result)
        }
    }

    protected abstract fun <T : DeviceConnection> getEscPosPrinterService(
        printerConnection: T,
        printContext: PrintContent,
        widthPaper: Float,
        context: Context,
    ): EscPosPrinter

    /*   open fun publishState(progress: Int) {
           _printerState.value = when (progress) {
               PROGRESS_CONNECTING -> PrinterState.Connecting(R.string.core_printer_state_message_connecting)
               PROGRESS_CONNECTED -> PrinterState.Connected(R.string.core_printer_state_message_connected)
               PROGRESS_PRINTING -> PrinterState.Printing(R.string.core_printer_state_message_printing)
               PROGRESS_PRINTED -> PrinterState.Printed(R.string.core_printer_state_message_finished)

               else -> PrinterState.Error(R.string.core_printer_state_result_message_finish_unknown_error)
           }

       }

       private fun handleResult(result: PrinterStatus) {

           _printerState.value = when (result.printerStatus) {
               FINISH_SUCCESS -> PrinterState.Finished(R.string.core_printer_state_result_message_finish_success)
               FINISH_NO_PRINTER -> PrinterState.Finished(R.string.core_printer_state_result_message_finish_no_printer)
               FINISH_PRINTER_DISCONNECTED -> PrinterState.Finished(R.string.core_printer_state_result_message_finish_printer_disconnected)
               FINISH_PARSER_ERROR -> PrinterState.Finished(R.string.core_printer_state_result_message_finish_parser_error)
               FINISH_ENCODING_ERROR -> PrinterState.Finished(R.string.core_printer_state_result_message_finish_parser_error)
               FINISH_BARCODE_ERROR -> PrinterState.Finished(R.string.core_printer_state_result_message_finish_barcode_error)
               else -> PrinterState.Finished(R.string.core_printer_state_result_message_finish_unknown_error)
           }



           when (result.printerStatus) {
               FINISH_SUCCESS -> onPrintFinished?.onSuccess(result.asyncEscPosPrinterService)
               else -> onPrintFinished?.onError(result.asyncEscPosPrinterService, result.printerStatus)
           }
       }*/
}