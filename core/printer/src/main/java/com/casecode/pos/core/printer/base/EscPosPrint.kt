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

import android.annotation.SuppressLint
import android.content.Context
import com.casecode.pos.core.common.di.ApplicationScope
import com.casecode.pos.core.firebase.services.LogService
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.dantsu.escposprinter.EscPosPrinter as EscPosPrinterLib

abstract class EscPosPrint {
    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var printerState: PrinterStateManager

    @Inject
    lateinit var logger: LogService

    abstract fun print(
        context: Context,
        printerInfo: PrinterInfo,
        printContent: PrintContent,
    )

    @SuppressLint("MissingPermission")
    open suspend fun takePrints(printer: EscPosPrinter): PrinterStatus {
        this@EscPosPrint.printerState.publishState(PrinterStatusCode.PROGRESS_CONNECTING)
        val connection: DeviceConnection? = printer.getPrinterConnection()
        delay(1000L)

        try {
            val escPosPrinter =
                EscPosPrinterLib(
                    connection,
                    printer.printerDpi,
                    printer.printerWidthMM,
                    printer.printerNbrCharactersPerLine,
                    EscPosCharsetEncoding("Cp864", 0x16),
                    /**
                     * encoding support arabic:
                     * Cp864
                     * Cp720
                     *
                     */
                )
            logger.log("PrinterEncodingName: ${escPosPrinter.encoding.name}")

            escPosPrinter.useEscAsteriskCommand(true)

            this@EscPosPrint.printerState.publishState(PrinterStatusCode.PROGRESS_PRINTING)

            val printLines = printer.getTextsToPrint()
            printLines.forEach { line ->
                Timber.e("lineTaskPrint: $line")
                escPosPrinter.printFormattedTextAndCut(line)
                delay(400)
            }

            this@EscPosPrint.printerState.publishState(PrinterStatusCode.PROGRESS_PRINTED)
            return PrinterStatus(printer, PrinterStatusCode.FINISH_SUCCESS)
        } catch (exception: Exception) {
            connection?.disconnect()
            logger.logNonFatalCrash(exception)
            return handleException(exception, printer)
        }
    }

    private fun handleException(
        exception: Exception,
        printer: EscPosPrinter,
    ): PrinterStatus {
        exception.printStackTrace()
        return when (exception) {
            is EscPosConnectionException ->
                PrinterStatus(
                    printer,
                    PrinterStatusCode.FINISH_PRINTER_DISCONNECTED,
                )

            is EscPosParserException ->
                PrinterStatus(
                    printer,
                    PrinterStatusCode.FINISH_PARSER_ERROR,
                )

            is EscPosEncodingException ->
                PrinterStatus(
                    printer,
                    PrinterStatusCode.FINISH_ENCODING_ERROR,
                )

            is EscPosBarcodeException ->
                PrinterStatus(
                    printer,
                    PrinterStatusCode.FINISH_BARCODE_ERROR,
                )

            else -> PrinterStatus(printer, PrinterStatusCode.FINISH_NO_PRINTER)
        }
    }

    open fun execute(printer: EscPosPrinter) {
        applicationScope.launch {
            this@EscPosPrint.printerState.handleResult(takePrints(printer))
        }
    }

    protected abstract fun <T : DeviceConnection> getEscPosPrinterService(
        deviceConnection: T,
        content: PrintContent,
        paperWidth: Float,
        context: Context,
    ): EscPosPrinter
}