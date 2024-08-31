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
    open suspend fun takePrints(printerData: EscPosPrinter): PrinterStatus {
        printerStateManager.publishState(PrinterStatusCode.PROGRESS_CONNECTING)
        val deviceConnection: DeviceConnection? = printerData.getPrinterConnection()

        try {
            val printer =
                EscPosPrinterLib(
                    deviceConnection,
                    printerData.printerDpi,
                    printerData.printerWidthMM,
                    printerData.printerNbrCharactersPerLine,
                    EscPosCharsetEncoding("Cp864", 16),
                )
            printer.useEscAsteriskCommand(true)

            printerStateManager.publishState(PrinterStatusCode.PROGRESS_PRINTING)

            val textsToPrint = printerData.getTextsToPrint()
            Timber.e("textsToPrint: $textsToPrint")
            textsToPrint.forEach { textToPrint ->
                Timber.e("textToPrint:foreach: $textToPrint")
                printer.printFormattedTextAndCut(textToPrint)
                delay(500)
            }

            printerStateManager.publishState(PrinterStatusCode.PROGRESS_PRINTED)
            return PrinterStatus(printerData, PrinterStatusCode.FINISH_SUCCESS)
        } catch (e: Exception) {
            deviceConnection?.disconnect()
            logService.logNonFatalCrash(e)
            return handleException(e, printerData)
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

}