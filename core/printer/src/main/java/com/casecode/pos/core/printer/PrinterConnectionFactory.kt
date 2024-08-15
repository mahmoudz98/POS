package com.casecode.pos.core.printer

import com.casecode.pos.core.model.data.PrinterConnectionType
import com.casecode.pos.core.printer.base.BluetoothEscPosPrint
import com.casecode.pos.core.printer.base.EscPosPrint
import com.casecode.pos.core.printer.base.EscPosPrinterService
import com.casecode.pos.core.printer.base.TcpEscPosPrint
import com.casecode.pos.core.printer.base.UsbEscPosPrint
import javax.inject.Inject

class PrinterConnectionFactory @Inject constructor(
    private val bluetoothPrinterConnection: BluetoothEscPosPrint,
    private val tcpPrinterConnection: TcpEscPosPrint,
    private val usbPrinterConnection: UsbEscPosPrint
) {
    fun create(printerType: PrinterConnectionType): EscPosPrint {
        return when (printerType) {
            PrinterConnectionType.BLUETOOTH -> bluetoothPrinterConnection
            PrinterConnectionType.ETHERNET -> tcpPrinterConnection
            PrinterConnectionType.USB -> usbPrinterConnection
        }
    }
}