package com.cassecode.pos.core.printer.base

import com.dantsu.escposprinter.EscPosPrinterSize
import com.dantsu.escposprinter.connection.DeviceConnection

class EscPosPrinterService(
    private var printerConnection: DeviceConnection,
    printerDpi: Int,
    printerWidthMM: Float,
    printerNbrCharactersPerLine: Int
) : EscPosPrinterSize(printerDpi, printerWidthMM, printerNbrCharactersPerLine) {

    private var textsToPrint: MutableList<String> = mutableListOf()

    fun getPrinterConnection(): DeviceConnection {
        return printerConnection
    }

    fun setTextsToPrint(textsToPrint: Array<String>): EscPosPrinterService {
        this.textsToPrint = textsToPrint.toMutableList()
        return this
    }

    fun addTextToPrint(textToPrint: String): EscPosPrinterService {
        textsToPrint.add(textToPrint)
        return this
    }

    fun getTextsToPrint(): Array<String> {
        return textsToPrint.toTypedArray()
    }
}