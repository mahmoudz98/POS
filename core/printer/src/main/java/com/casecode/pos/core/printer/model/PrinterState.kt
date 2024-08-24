package com.casecode.pos.core.printer.model

sealed class PrinterState(
    open val message: Int? = null,
) {
    object None : PrinterState()

    data class Connecting(
        override val message: Int,
    ) : PrinterState(message)

    data class Connected(
        override val message: Int,
    ) : PrinterState(message)

    data class Printing(
        override val message: Int,
    ) : PrinterState(message)

    data class Printed(
        override val message: Int,
    ) : PrinterState(message)

    data class Error(
        override val message: Int,
    ) : PrinterState(message)

    data class Finished(
        override val message: Int,
    ) : PrinterState(message)
}