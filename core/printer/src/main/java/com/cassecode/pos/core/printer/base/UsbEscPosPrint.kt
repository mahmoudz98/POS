package com.cassecode.pos.core.printer.base

import android.content.Context

class UsbEscPosPrint(
    context: Context,
    onPrintFinished: OnPrintFinished? = null
) : EscPosPrint(context, onPrintFinished)