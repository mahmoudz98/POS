package com.casecode.pos.feature.setting.printer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.domain.usecase.AddPrinterUseCase
import com.casecode.pos.core.domain.usecase.GetPrinterUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.PrinterConnectionInfo
import com.casecode.pos.core.model.data.PrinterConnectionType
import com.casecode.pos.core.model.data.PrinterInfo
import com.casecode.pos.core.model.data.toConnectionType
import com.cassecode.pos.core.printer.PrintContent
import com.cassecode.pos.core.printer.PrinterConnection
import com.cassecode.pos.core.printer.PrinterConnectionFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrinterVIewModel @Inject constructor(
    private val addPrinterUseCase: AddPrinterUseCase,
    private val getPrinterUseCase: GetPrinterUseCase,
    private val printerConnectionFactory: PrinterConnectionFactory,
) : ViewModel() {
    val printersUiState: StateFlow<Resource<List<PrinterInfo>>> = getPrinterUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Resource.Loading,
        )

    private lateinit var printerConnection: PrinterConnection

    fun addPrinter(printerInfo: PrinterInfo) {
        viewModelScope.launch {
            addPrinterUseCase(printerInfo)
        }
    }

    fun testPrinter(
        typePrinterConnection: String,
        namePrinter: String,
        ipAddress: String,
        port: String,
        macAddress: String,
        usbDeviceName: String,
        isCurrentSelected: Boolean,
        paperWidth: String,
        context: android.content.Context,
    ) {
        val printerType = typePrinterConnection.toConnectionType()
        when (printerType!!) {
            PrinterConnectionType.ETHERNET -> {
                testPrinterEthernet(
                    namePrinter,
                    ipAddress,
                    port,
                    isCurrentSelected,
                    paperWidth,
                    context,
                )
            }

            PrinterConnectionType.BLUETOOTH -> {
                testPrinterBluetooth(
                    namePrinter,
                    macAddress,
                    isCurrentSelected,
                    paperWidth,
                    context,
                )
            }

            PrinterConnectionType.USB -> {
                testPrinterUsb(
                    namePrinter,
                    usbDeviceName,
                    isCurrentSelected,
                    paperWidth,
                    context,
                )
            }

        }


    }

    fun testPrinterEthernet(
        namePrinter: String,
        ipAddress: String,
        port: String,
        isCurrentSelected: Boolean,
        paperWidth: String,
        context: android.content.Context,
    ) {

        val printerInfo = PrinterInfo(
            name = namePrinter,
            connectionTypeInfo = PrinterConnectionInfo.Tcp(ipAddress, port.toInt()),
            isCurrentSelected = isCurrentSelected,
            size = paperWidth,
        )
        printerConnection = printerConnectionFactory.create(PrinterConnectionType.ETHERNET)
        printerConnection.print(context, printerInfo, PrintContent.Test)
    }

    fun testPrinterBluetooth(
        namePrinter: String,
        macAddress: String,
        isCurrentSelected: Boolean,
        paperWidth: String,
        context: android.content.Context,
    ) {

        val printerInfo = PrinterInfo(
            name = namePrinter,
            connectionTypeInfo = PrinterConnectionInfo.Bluetooth(macAddress),
            isCurrentSelected = isCurrentSelected,
            size = paperWidth,
        )
        printerConnection = printerConnectionFactory.create(PrinterConnectionType.BLUETOOTH)
        printerConnection.print(context, printerInfo, PrintContent.Test)

    }

    fun testPrinterUsb(
        namePrinter: String,
        usbDeviceName: String,
        isCurrentSelected: Boolean,
        paperWidth: String,
        context: android.content.Context,
    ) {

        val printerInfo = PrinterInfo(
            name = namePrinter,
            connectionTypeInfo = PrinterConnectionInfo.Usb(usbDeviceName),
            isCurrentSelected = isCurrentSelected,
            size = paperWidth,
        )
        printerConnection = printerConnectionFactory.create(PrinterConnectionType.USB)
        printerConnection.print(context, printerInfo, PrintContent.Test)

    }


}