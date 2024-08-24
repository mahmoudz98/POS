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