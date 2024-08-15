package com.casecode.pos.core.printer.base

interface OnPrintFinished {
    fun onError(asyncEscPosPrinterService: EscPosPrinterService?, codeException: Int)
    fun onSuccess(asyncEscPosPrinterService: EscPosPrinterService?)
}