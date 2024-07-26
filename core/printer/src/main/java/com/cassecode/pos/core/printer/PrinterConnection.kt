package com.cassecode.pos.core.printer

import android.content.Context
import com.casecode.pos.core.model.data.PrinterInfo


interface PrinterConnection {
    fun print(context: Context, printerInfo: PrinterInfo, printContext: PrintContent)
}