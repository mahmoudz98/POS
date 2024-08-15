package com.casecode.pos.core.model.data
sealed class PrinterConnectionInfo {
    data class Tcp(val ipAddress: String, val port: Int) : PrinterConnectionInfo()
    data class Bluetooth(val name:String,val macAddress: String) : PrinterConnectionInfo()
    data class Usb(val usbDeviceName: String) : PrinterConnectionInfo()
}
data class PrinterInfo(
    val name: String,
    val connectionTypeInfo: PrinterConnectionInfo,
    val isCurrentSelected: Boolean,
    val widthPaper: String,
)