package com.casecode.pos.core.model.data


data class PrinterInfo(
    val name: String,
    val connectionType: ConnectionType,
    val address: String, // Address can be IP or MAC address
    val port: Int? = null,
    val isCurrentSelected: Boolean,
    val size: String,
)

enum class ConnectionType {
    BLUETOOTH,
    USB,
    ETHERNET
}