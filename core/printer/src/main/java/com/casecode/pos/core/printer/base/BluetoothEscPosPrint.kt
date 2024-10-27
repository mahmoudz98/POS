/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import com.casecode.pos.core.printer.connection.BluetoothConnectionImpl
import com.casecode.pos.core.printer.model.PrintContent
import com.casecode.pos.core.printer.model.PrinterState
import com.casecode.pos.core.printer.model.PrinterStatus
import com.casecode.pos.core.printer.model.PrinterStatusCode
import com.casecode.pos.core.printer.utils.PrintUtils
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class BluetoothEscPosPrint
@Inject
constructor() : EscPosPrint() {
    @SuppressLint("MissingPermission")
    override suspend fun takePrints(printerData: EscPosPrinter): PrinterStatus {
        Timber.e("takePrints:BluetoothEscPosPrint")
        this@BluetoothEscPosPrint.printerState.publishState(PrinterStatusCode.PROGRESS_CONNECTING)
        val deviceConnection = (printerData.getPrinterConnection() as? BluetoothConnectionImpl)

        val updatedPrinterData: EscPosPrinter =
            if (deviceConnection == null) {
                Timber.e("No paired Bluetooth devices found")
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
                    if (!deviceConnection.isConnected) {
                        attemptConnection(deviceConnection)
                    }
                    printerData
                } catch (e: EscPosConnectionException) {
                    return handleConnectionException(e, deviceConnection, printerData)
                }
            }
        return super.takePrints(updatedPrinterData)
    }

    @Throws(IOException::class)
    private fun attemptConnection(deviceConnection: BluetoothConnectionImpl) {
        try {
            deviceConnection.connect()
        } catch (e: EscPosConnectionException) {
            deviceConnection.disconnect()
            throw e // Rethrow the exception after all retries are exhausted
        }
    }

    @SuppressLint("MissingPermission")
    private fun handleConnectionException(
        e: EscPosConnectionException,
        deviceConnection: BluetoothConnectionImpl,
        printerData: EscPosPrinter,
    ): PrinterStatus {
        e.printStackTrace()
        logger.logNonFatalCrash(e)
        val log = """
            deviceConnection.deviceStatus=${deviceConnection.device?.name},
             ${deviceConnection.device?.address}
        """.trimIndent()
        logger.log(log)
        logger.log("TextsToPrint = ${printerData.getTextsToPrint().first()}")
        return PrinterStatus(
            printerData,
            PrinterStatusCode.FINISH_PRINTER_DISCONNECTED,
        )
    }

    override fun print(
        context: Context,
        printerInfo: PrinterInfo,
        printContent: PrintContent,
    ) {
        val bluetoothConnection =
            BluetoothConnectionImpl(
                getBluetoothDeviceByMacAddress(
                    context,
                    (printerInfo.connectionTypeInfo as PrinterConnectionInfo.Bluetooth).macAddress,
                ),
                context,
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
            this@BluetoothEscPosPrint.printerState.printerState.value =
                PrinterState.Error(R.string.core_printer_state_bluetooth_message_error)
            Timber.e("Device doesn't support Bluetooth")
            return null
        }

        if (!bluetoothAdapter.isEnabled) {
            this@BluetoothEscPosPrint.printerState.printerState.value =
                PrinterState.Error(R.string.core_printer_state_bluetooth_message_error_not_enabled)
            Timber.e("Bluetooth is not enabled")
            return null
        }

        return try {
            bluetoothAdapter.getRemoteDevice(macAddress)
        } catch (e: IllegalArgumentException) {
            this@BluetoothEscPosPrint.printerState.printerState.value =
                PrinterState.Error(R.string.core_printer_state_bluetooth_message_error_invalid_mac_address)
            Timber.e("Invalid MAC address: $macAddress")
            e.printStackTrace()
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
                        PrinterTextParserImg.bitmapToHexadecimalString(
                            this,
                            context.resources.getDrawableForDensity(
                                R.drawable.core_printer_ic_point_of_sale_24,
                                DisplayMetrics.DENSITY_MEDIUM,
                                null,
                            ),
                        )
                        PrintUtils.testWithoutLogo()

                        // PrintUtils.test(logo)
                    }
                }
            Timber.e("textToPrint: $textToPrint")
            addTextToPrint(textToPrint)
        }
}