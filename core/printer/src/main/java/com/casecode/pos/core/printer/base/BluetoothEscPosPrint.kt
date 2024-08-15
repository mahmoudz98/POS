package com.casecode.pos.core.printer.base

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.DisplayMetrics
import com.casecode.pos.core.model.data.PrinterConnectionInfo
import com.casecode.pos.core.model.data.PrinterInfo
import com.casecode.pos.core.printer.PrintContent
import com.casecode.pos.core.printer.R
import com.casecode.pos.core.printer.utils.PrintUtils
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class BluetoothEscPosPrint @Inject constructor(
) : EscPosPrint() {

    override suspend fun takePrints(printerData: EscPosPrinterService): PrinterStatus =
        withContext(Dispatchers.IO) {
            val deviceConnection = printerData.getPrinterConnection()
            publishState(PROGRESS_CONNECTING)
            if (deviceConnection == null) {
                val newPrinterConnection = BluetoothPrintersConnections.selectFirstPaired()
                val updatedPrinterData = newPrinterConnection?.let {
                    EscPosPrinterService(
                        it,
                        printerData.printerDpi,
                        printerData.printerWidthMM,
                        printerData.printerNbrCharactersPerLine,
                    ).apply {
                        setTextsToPrint(printerData.getTextsToPrint())
                    }
                }

                // Try to connect to the new Bluetooth printer
                try {
                    newPrinterConnection?.connect()
                } catch (e: EscPosConnectionException) {
                    e.printStackTrace()
                    return@withContext PrinterStatus(
                        updatedPrinterData,
                        FINISH_PRINTER_DISCONNECTED,
                    )
                }

                // Continue with the updated printer data
                super.takePrints(updatedPrinterData!!)
            } else {
                printerData.getPrinterConnection()
                super.takePrints(printerData)
            }
        }

    override fun publishState(progress: Int) {
        super.publishState(progress)
        CoroutineScope(Dispatchers.Main).launch {
            /*   dialog?.apply {
                   when (progress) {
                       PROGRESS_CONNECTING -> setMessage("Connecting to Bluetooth printer...")
                       PROGRESS_CONNECTED -> setMessage("Bluetooth printer is connected...")
                       PROGRESS_PRINTING -> setMessage("Bluetooth printer is printing...")
                       PROGRESS_PRINTED -> setMessage("Bluetooth printer has finished...")
                   }
                   setProgress(progress)
                   setMax(4)
               }*/
        }
    }

    override fun print(context: Context, printerInfo: PrinterInfo, printContent: PrintContent) {
        val bluetoothConnection =
            BluetoothConnection(
                getBluetoothDeviceByMacAddress(
                    context,
                    (printerInfo.connectionTypeInfo as PrinterConnectionInfo.Bluetooth).macAddress,
                ),
            )

        execute(getEscPosPrinterService(bluetoothConnection, printContent, printerInfo.widthPaper.toFloat(), context))

    }

    private fun getBluetoothDeviceByMacAddress(
        context: Context,
        macAddress: String,
    ): BluetoothDevice? {
        val bluetoothManager: BluetoothManager? =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

        if (bluetoothAdapter == null) {
            super._printerState.value =
                PrinterState.Error(R.string.core_printer_state_bluetooth_message_error)
            Timber.e("Device doesn't support Bluetooth")
            return null
        }

        if (!bluetoothAdapter.isEnabled) {
            super._printerState.value =
                PrinterState.Error(R.string.core_printer_state_bluetooth_message_error_not_enabled)
            Timber.e("Bluetooth is not enabled")
            return null
        }

        return try {
            bluetoothAdapter.getRemoteDevice(macAddress)
        } catch (e: IllegalArgumentException) {
            super._printerState.value =
                PrinterState.Error(R.string.core_printer_state_bluetooth_message_error_invalid_mac_address)
            Timber.e("Invalid MAC address: $macAddress")
            null
        }
    }


    private fun getEscPosPrinterService(
        printerConnection: BluetoothConnection,
        printContext: PrintContent,
        widthPaper: Float,
        context: Context,
    ): EscPosPrinterService {
        return EscPosPrinterService(
            printerConnection, printerDpi = 203,
            printerWidthMM = widthPaper, printerNbrCharactersPerLine = 32,
        ).apply {

            val textToPrint = when (printContext) {
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
                    val logo = PrinterTextParserImg.bitmapToHexadecimalString(
                        this,
                        context.resources.getDrawableForDensity(
                            R.drawable.core_printer_ic_point_of_sale_24,
                            DisplayMetrics.DENSITY_MEDIUM,
                        ),
                    )
                    PrintUtils.test(logo)
                }
            }
            Timber.e("textToPrint: $textToPrint")
            addTextToPrint(textToPrint)
        }
    }


}