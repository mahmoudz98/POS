package com.cassecode.pos.core.printer.base

import android.content.Context

class TcpEscPosPrint(
    context: Context,
    onPrintFinished: OnPrintFinished? = null
) : EscPosPrint(context, onPrintFinished)