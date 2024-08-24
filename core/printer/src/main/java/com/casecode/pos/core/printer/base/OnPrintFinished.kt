package com.casecode.pos.core.printer.base

interface OnPrintFinished {
    fun onError(
        asyncEscPosPrinterService: EscPosPrinter?,
        codeException: Int,
    )

    fun onSuccess(asyncEscPosPrinterService: EscPosPrinter?)
}