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

import com.dantsu.escposprinter.EscPosPrinterSize
import com.dantsu.escposprinter.connection.DeviceConnection

/**
 * Service for interacting with an EscPos printer.
 *
 * @param printerConnection The connection to the printer.
 * @param printerDpi The DPI of the printer.
 * @param printerWidthMM The width of the printer in millimeters.
 * @param printerNbrCharactersPerLine The number of characters that can be printed per line.
 */
class EscPosPrinter(
    private var printerConnection: DeviceConnection?,
    printerDpi: Int,
    printerWidthMM: Float,
    printerNbrCharactersPerLine: Int,
) : EscPosPrinterSize(printerDpi, printerWidthMM, printerNbrCharactersPerLine) {
    private var textsToPrint: MutableList<String> = mutableListOf()

    fun getPrinterConnection(): DeviceConnection? = printerConnection

    fun setTextsToPrint(textsToPrint: Array<String>): EscPosPrinter {
        this.textsToPrint = textsToPrint.toMutableList()
        return this
    }

    fun addTextToPrint(textToPrint: String): EscPosPrinter {
        textsToPrint.add(textToPrint)
        return this
    }

    fun getTextsToPrint(): Array<String> = textsToPrint.toTypedArray()
}