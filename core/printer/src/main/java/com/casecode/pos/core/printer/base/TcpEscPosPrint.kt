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

import android.content.Context
import android.util.DisplayMetrics
import com.casecode.pos.core.model.data.PrinterConnectionInfo
import com.casecode.pos.core.model.data.PrinterInfo
import com.casecode.pos.core.printer.R
import com.casecode.pos.core.printer.model.PrintContent
import com.casecode.pos.core.printer.model.PrinterState
import com.casecode.pos.core.printer.utils.PrintUtils
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.tcp.TcpConnection
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import timber.log.Timber
import javax.inject.Inject

class TcpEscPosPrint
@Inject
constructor() : EscPosPrint() {
    companion object {
        private const val TIMEOUT_CONNECTION = 6000
    }

    override fun print(
        context: Context,
        printerInfo: PrinterInfo,
        printContent: PrintContent,
    ) {
        val ipAddress = (printerInfo.connectionTypeInfo as PrinterConnectionInfo.Tcp).ipAddress
        val portAddress = (printerInfo.connectionTypeInfo as PrinterConnectionInfo.Tcp).port
        try {
            execute(
                getEscPosPrinterService(
                    TcpConnection(ipAddress, portAddress, TIMEOUT_CONNECTION),
                    printContent,
                    printerInfo.widthPaper.toFloat(),
                    context,
                ),
            )
        } catch (e: NumberFormatException) {
            Timber.e("Invalid TCP port address")
            e.printStackTrace()
            this@TcpEscPosPrint.printerState.printerState.value =
                PrinterState.Error(R.string.core_printer_state_result_message_finish_parser_error)
        }
    }

    override fun <T : DeviceConnection> getEscPosPrinterService(
        printerConnection: T,
        printContext: PrintContent,
        widthPaper: Float,
        context: Context,
    ): EscPosPrinter {
        // TODO:Handle paper size
        return EscPosPrinter(printerConnection, 203, widthPaper, 32).apply {
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
}