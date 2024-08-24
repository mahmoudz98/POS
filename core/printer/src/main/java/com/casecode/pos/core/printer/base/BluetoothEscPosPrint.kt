package com.casecode.pos.core.printer.base

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.DisplayMetrics
import com.casecode.pos.core.model.data.PrinterConnectionInfo
import com.casecode.pos.core.model.data.PrinterInfo
import com.casecode.pos.core.printer.R
import com.casecode.pos.core.printer.model.PrintContent
import com.casecode.pos.core.printer.model.PrinterState
import com.casecode.pos.core.printer.model.PrinterStatus
import com.casecode.pos.core.printer.model.PrinterStatusCode
import com.casecode.pos.core.printer.utils.PrintUtils
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class BluetoothEscPosPrint
    @Inject
    constructor() : EscPosPrint() {
        @SuppressLint("MissingPermission")
        override suspend fun takePrints(printerData: EscPosPrinter): PrinterStatus =
            withContext(Dispatchers.IO) {
                Timber.e("takePrints:BluetoothEscPosPrint")
                printerStateManager.publishState(PrinterStatusCode.PROGRESS_CONNECTING)
                val deviceConnection = (printerData.getPrinterConnection() as BluetoothConnection?)

                val updatedPrinterData: EscPosPrinter =
                    if (deviceConnection == null) {
                        val pairedDevice = BluetoothPrintersConnections.selectFirstPaired()
                        EscPosPrinter(
                            pairedDevice,
                            printerData.printerDpi,
                            printerData.printerWidthMM,
                            printerData.printerNbrCharactersPerLine,
                        ).apply {
                            setTextsToPrint(printerData.getTextsToPrint())
                        }
                    } else {
                        try {
                            deviceConnection.connect()
                            printerData
                        } catch (e: EscPosConnectionException) {
                            e.printStackTrace()
                            logService.logNonFatalCrash(e)
                            val log =
                                """ 
                                deviceConnection.deviceName=${deviceConnection.device.name}
                                deviceConnection.deviceAddress=${deviceConnection.device.address}
                                deviceConnection.isConnected=${deviceConnection.isConnected}
                                """.trimIndent()
                            logService.log(log)
                            logService.log("TextsToPrint = ${printerData.getTextsToPrint().map { it }}")
                            return@withContext PrinterStatus(
                                printerData,
                                PrinterStatusCode.FINISH_PRINTER_DISCONNECTED,
                            )
                        }
                    }

                super.takePrints(updatedPrinterData)
            }

        override fun print(
            context: Context,
            printerInfo: PrinterInfo,
            printContent: PrintContent,
        ) {
            val bluetoothConnection =
                BluetoothConnection(
                    getBluetoothDeviceByMacAddress(
                        context,
                        (printerInfo.connectionTypeInfo as PrinterConnectionInfo.Bluetooth).macAddress,
                    ),
                )

            execute(
                getEscPosPrinterService(
                    bluetoothConnection,
                    printContent,
                    printerInfo.widthPaper.toFloat(),
                    context,
                ),
            )
        }

        private fun getBluetoothDeviceByMacAddress(
            context: Context,
            macAddress: String,
    ): BluetoothDevice? {
        val bluetoothManager: BluetoothManager? =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

        if (bluetoothAdapter == null) {
            printerStateManager._printerState.value =
                PrinterState.Error(R.string.core_printer_state_bluetooth_message_error)
            Timber.e("Device doesn't support Bluetooth")
            return null
        }

        if (!bluetoothAdapter.isEnabled) {
            printerStateManager._printerState.value =
                PrinterState.Error(R.string.core_printer_state_bluetooth_message_error_not_enabled)
            Timber.e("Bluetooth is not enabled")
            return null
        }

        return try {
            bluetoothAdapter.getRemoteDevice(macAddress)
        } catch (e: IllegalArgumentException) {
            printerStateManager._printerState.value =
                PrinterState.Error(R.string.core_printer_state_bluetooth_message_error_invalid_mac_address)
            Timber.e("Invalid MAC address: $macAddress")
            null
        }
    }

    override fun <T : DeviceConnection> getEscPosPrinterService(
        printerConnection: T,
        printContext: PrintContent,
        widthPaper: Float,
        context: Context,
    ): EscPosPrinter =
        EscPosPrinter(
            printerConnection,
            printerDpi = 203,
            printerWidthMM = widthPaper,
            printerNbrCharactersPerLine = 32,
        ).apply {
            val textToPrint =
                when (printContext) {
                    is PrintContent.Receipt -> {
                        PrintUtils.generatePrintText(
                            printContext.invoiceId,
                            printContext.phone,
                            printContext.items,
                        )
                    }

                    is PrintContent.QrCode -> {
                        PrintUtils.generateBarcode(printContext.item)
                    }

                    is PrintContent.Test -> {
                        val logo =
                            PrinterTextParserImg.bitmapToHexadecimalString(
                                this,
                                context.resources.getDrawableForDensity(
                                    R.drawable.core_printer_ic_point_of_sale_24,
                                    DisplayMetrics.DENSITY_MEDIUM,
                                    null,
                                ),
                            )
                        PrintUtils.test(logo)
                    }
                }
            Timber.e("textToPrint: $textToPrint")
            addTextToPrint(textToPrint)
        }
    }