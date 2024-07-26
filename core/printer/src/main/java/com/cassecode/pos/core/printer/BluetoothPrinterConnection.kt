package com.cassecode.pos.core.printer

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import com.casecode.pos.core.model.data.PrinterInfo
import com.cassecode.pos.core.printer.base.BluetoothEscPosPrint
import com.cassecode.pos.core.printer.base.EscPosPrinterService
import com.cassecode.pos.core.printer.base.OnPrintFinished
import com.cassecode.pos.core.printer.utils.PrintUtils
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import timber.log.Timber
import javax.inject.Inject

class BluetoothPrinterConnection @Inject constructor() : PrinterConnection {
  /*  private fun checkBluetoothPermissions(onPermissionsGranted: () -> Unit) {
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED -> {
                requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH)
            }
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED -> {
                requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_ADMIN)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED -> {
                requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED -> {
                requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_SCAN)
            }
            else -> onPermissionsGranted()
        }
    }*/
    private var selectedDevice: BluetoothConnection? = null

    override fun print(context: Context, printerInfo: PrinterInfo, printContext: PrintContent) {
        if (selectedDevice == null) {
            browseBluetoothDevice(context)
        } else {
            BluetoothEscPosPrint(
                context,
                object : OnPrintFinished {
                    override fun onError(
                        asyncEscPosPrinterService: EscPosPrinterService?,
                        codeException: Int,
                    ) {
                        Timber.tag("Async.OnPrintFinished").e("An error occurred!")
                    }

                    override fun onSuccess(asyncEscPosPrinterService: EscPosPrinterService?) {
                        Timber.tag("Async.OnPrintFinished").i("Print is finished!")
                    }
                },
            ).execute(getAsyncEscPosPrinter(selectedDevice!!, printContext))
        }

    }

    @SuppressLint("MissingPermission")
    private fun browseBluetoothDevice(context: Context) {
        val bluetoothDevicesList = BluetoothPrintersConnections().list
        bluetoothDevicesList?.let {
            val items = arrayOfNulls<String>(it.size + 1)
            items[0] = "Default printer"
            it.forEachIndexed { index, device ->
                items[index + 1] = device.device.name
            }
            AlertDialog.Builder(context)
                .setTitle("Bluetooth printer selection")
                .setItems(items) { _, i ->
                    val index = i - 1
                    selectedDevice = if (index == 0) null else bluetoothDevicesList[i - 1]
                }
                .show()
        }
    }

    private fun getAsyncEscPosPrinter(
        printerConnection: BluetoothConnection,
        printContext: PrintContent,

        ): EscPosPrinterService {
        return EscPosPrinterService(printerConnection, 203, 48f, 32).apply {

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
            }

            addTextToPrint(textToPrint)
        }
    }
}