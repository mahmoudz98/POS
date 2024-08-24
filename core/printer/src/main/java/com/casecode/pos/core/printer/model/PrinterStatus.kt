package com.casecode.pos.core.printer.model

import com.casecode.pos.core.printer.base.EscPosPrinter

data class PrinterStatus(
    val asyncEscPosPrinterService: EscPosPrinter?,
    val printerStatus: PrinterStatusCode,
)