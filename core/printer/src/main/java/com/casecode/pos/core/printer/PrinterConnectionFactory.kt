package com.casecode.pos.core.printer

import com.casecode.pos.core.model.data.PrinterConnectionType
import com.casecode.pos.core.printer.base.BluetoothEscPosPrint
import com.casecode.pos.core.printer.base.EscPosPrint
import com.casecode.pos.core.printer.base.TcpEscPosPrint
import com.casecode.pos.core.printer.base.UsbEscPosPrint
import javax.inject.Inject

class PrinterConnectionFactory
    @Inject
    constructor(
        private val bluetoothEscPosPrint: BluetoothEscPosPrint,
        private val tcpEscPosPrint: TcpEscPosPrint,
        private val usbEscPosPrint: UsbEscPosPrint,
    ) {
        fun create(printerType: PrinterConnectionType): EscPosPrint =
            when (printerType) {
                PrinterConnectionType.BLUETOOTH -> bluetoothEscPosPrint
                PrinterConnectionType.ETHERNET -> tcpEscPosPrint
                PrinterConnectionType.USB -> usbEscPosPrint
        }
}