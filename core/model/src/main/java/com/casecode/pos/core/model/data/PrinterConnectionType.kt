package com.casecode.pos.core.model.data

enum class PrinterConnectionType(val en:String, val ar:String) {

    BLUETOOTH("Bluetooth","بلوتوث"),
    USB("Usb", "Usb"),
    ETHERNET("Ethernet","إيثرنت");


}
fun String.toConnectionType(): PrinterConnectionType? {
    return PrinterConnectionType.entries.find { type ->
        type.ar == this || type.en.lowercase() == this.lowercase()
    }
}