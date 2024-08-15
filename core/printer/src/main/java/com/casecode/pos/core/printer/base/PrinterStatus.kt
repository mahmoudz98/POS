package com.casecode.pos.core.printer.base

data class PrinterStatus(
    val asyncEscPosPrinterService: EscPosPrinterService?,
    val printerStatus: Int,
)