package com.casecode.pos.core.printer.base

import android.content.Context
import com.casecode.pos.core.model.data.PrinterInfo
import com.casecode.pos.core.printer.PrintContent
import com.casecode.pos.core.printer.R
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


abstract class EscPosPrint(
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

    private val onPrintFinished: OnPrintFinished? = null

    internal val _printerState = MutableStateFlow<PrinterState>(PrinterState.None)
    val printerState: StateFlow<PrinterState> = _printerState.asStateFlow()

    abstract fun print(context: Context, printerInfo: PrinterInfo, printContent: PrintContent)

    open suspend fun takePrints(printerData: EscPosPrinterService): PrinterStatus =
        withContext(Dispatchers.IO) {
            publishState(PROGRESS_CONNECTING)
            try {
                val deviceConnection: DeviceConnection = printerData.getPrinterConnection()
                val printer = EscPosPrinter(
                    deviceConnection,
                    printerData.printerDpi,
                    printerData.printerWidthMM,
                    printerData.printerNbrCharactersPerLine,
                    EscPosCharsetEncoding("windows-1252", 16),
                )
                printer.useEscAsteriskCommand(true)

                publishState(PROGRESS_PRINTING)

                val textsToPrint = printerData.getTextsToPrint()
                Timber.e("textsToPrint: $textsToPrint")
                textsToPrint.forEach { textToPrint ->
                    Timber.e("textToPrint:foreach: $textToPrint")
                    printer.printFormattedTextAndCut(textToPrint)
                    delay(500)
                }

                publishState(PROGRESS_PRINTED)
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
        CoroutineScope(Dispatchers.Main).launch {
            val result = takePrints(printerData)
            handleResult(result)
        }
    }

    open fun publishState(progress: Int) {
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
    }
}