package com.cassecode.pos.core.printer

import com.casecode.pos.core.model.data.PrinterConnectionType
import javax.inject.Inject

class PrinterConnectionFactory @Inject constructor(
    private val bluetoothPrinterConnection: BluetoothPrinterConnection,
    private val tcpPrinterConnection: TcpPrinterConnection,
    private val usbPrinterConnection: UsbPrinterConnection
) {
    fun create(printerType: PrinterConnectionType): PrinterConnection {
        return when (printerType) {
            PrinterConnectionType.BLUETOOTH -> bluetoothPrinterConnection
            PrinterConnectionType.ETHERNET -> tcpPrinterConnection
            PrinterConnectionType.USB -> usbPrinterConnection
        }
    }
}