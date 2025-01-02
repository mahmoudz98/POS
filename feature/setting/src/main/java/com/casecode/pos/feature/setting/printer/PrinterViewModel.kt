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
package com.casecode.pos.feature.setting.printer

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.domain.usecase.AddPrinterUseCase
import com.casecode.pos.core.domain.usecase.GetPrinterUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.PrinterConnectionInfo
import com.casecode.pos.core.model.data.PrinterConnectionType
import com.casecode.pos.core.model.data.PrinterInfo
import com.casecode.pos.core.printer.PrinterConnectionFactory
import com.casecode.pos.core.printer.base.EscPosPrint
import com.casecode.pos.core.printer.model.PrintContent
import com.casecode.pos.core.printer.model.PrinterState
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrinterViewModel
@Inject
constructor(
    private val addPrinterUseCase: AddPrinterUseCase,
    private val getPrinterUseCase: GetPrinterUseCase,
    private val printerConnectionFactory: PrinterConnectionFactory,
    // private val logService: LogService,
) : ViewModel() {
    private val _addPrinterResult = MutableStateFlow<Resource<Int>?>(null)
    val addPrinterResult: StateFlow<Resource<Int>?> = _addPrinterResult
    val printersUiState: StateFlow<Resource<List<PrinterInfo>>> =
        getPrinterUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = Resource.Loading,
            )
    private lateinit var printerConnection: EscPosPrint
    var printerState: MutableStateFlow<PrinterState> = MutableStateFlow(PrinterState.None)

    private fun launchCatching(block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch(
        CoroutineExceptionHandler { _, throwable ->
            //    logService.logNonFatalCrash(throwable)
        },
        block = block,
    )

    fun addPrinter(printerInfo: PrinterInfo) {
        launchCatching {
            _addPrinterResult.value = Resource.loading()
            delay(1000)
            _addPrinterResult.value = addPrinterUseCase(printerInfo)
        }
    }

    /*fun testPrinter(
        typePrinterConnection: String,
        namePrinter: String,
        ipAddress: String?,
        port: String?,
        bluetoothConnection: BluetoothConnection?,
        usbDeviceName: String?,
        isCurrentSelected: Boolean,
        paperWidth: String,
        context: android.content.Context,
    ) {
            when (typePrinterConnection.toConnectionType()) {
                PrinterConnectionType.ETHERNET -> {
                    if (ipAddress != null && port != null) {
                        testPrinterEthernet(
                            namePrinter,
                            ipAddress,
                            port,
                            isCurrentSelected,
                            paperWidth,
                            context,
                        )

                    }
                }

                PrinterConnectionType.BLUETOOTH -> {
                    if (bluetoothConnection != null) {
                        testPrinterBluetooth(
                            namePrinter,
                            bluetoothConnection,
                            isCurrentSelected,
                            paperWidth,
                            context,
                        )
                    }
                }

                PrinterConnectionType.USB -> {
                    if (usbDeviceName != null) {
                        testPrinterUsb(
                            namePrinter,
                            usbDeviceName,
                            isCurrentSelected,
                            paperWidth,
                            context,
                        )
                    }
                }

                null -> {

                }
            }

        launchCatching{
            printerConnection.printerState.printerState.collect {
                printerState.value = it
            }
        }

    }*/
    fun testPrinterEthernet(
        namePrinter: String,
        ipAddress: String,
        port: String,
        paperWidth: String,
        context: android.content.Context,
    ) {
        val printerInfo =
            PrinterInfo(
                name = namePrinter,
                connectionTypeInfo = PrinterConnectionInfo.Tcp(ipAddress, port.toInt()),
                isDefaultPrint = false,
                widthPaper = paperWidth,
            )
        printerConnection = printerConnectionFactory.create(PrinterConnectionType.ETHERNET)
        printerConnection.print(context, printerInfo, PrintContent.Test)

        collectPrinterState()
    }

    fun addPrinterInfoBluetooth(
        namePrinter: String,
        nameDevice: String,
        address: String,
        paperWidth: String,
    ) {
        val printerInfo =
            PrinterInfo(
                name = namePrinter,
                connectionTypeInfo =
                PrinterConnectionInfo.Bluetooth(
                    nameDevice,
                    address,
                ),
                isDefaultPrint = false,
                widthPaper = paperWidth,
            )
        addPrinter(printerInfo)
    }

    fun addPrinterInfoUsb(namePrinter: String, nameDevice: String, paperWidth: String) {
        val printerInfo =
            PrinterInfo(
                name = namePrinter,
                connectionTypeInfo = PrinterConnectionInfo.Usb(nameDevice),
                isDefaultPrint = false,
                widthPaper = paperWidth,
            )
        addPrinter(printerInfo)
    }

    fun addPrinterInfoEthernet(
        namePrinter: String,
        ipAddress: String,
        port: String,
        paperWidth: String,
    ) {
        val printerInfo =
            PrinterInfo(
                name = namePrinter,
                connectionTypeInfo = PrinterConnectionInfo.Tcp(ipAddress, port.toInt()),
                isDefaultPrint = false,
                widthPaper = paperWidth,
            )
        addPrinter(printerInfo)
    }

    @SuppressLint("MissingPermission")
    fun testPrinterBluetooth(
        namePrinter: String,
        bluetoothConnection: BluetoothConnection,
        paperWidth: String,
        context: android.content.Context,
    ) {
        val printerInfo =
            PrinterInfo(
                name = namePrinter,
                connectionTypeInfo =
                PrinterConnectionInfo.Bluetooth(
                    bluetoothConnection.device.name,
                    bluetoothConnection.device.address,
                ),
                isDefaultPrint = false,
                widthPaper = paperWidth,
            )
        printerConnection = printerConnectionFactory.create(PrinterConnectionType.BLUETOOTH)
        printerConnection.print(context, printerInfo, PrintContent.Test)
        collectPrinterState()
    }

    fun testPrinterUsb(
        namePrinter: String,
        usbDeviceName: String,
        paperWidth: String,
        context: android.content.Context,
    ) {
        val printerInfo =
            PrinterInfo(
                name = namePrinter,
                connectionTypeInfo = PrinterConnectionInfo.Usb(usbDeviceName),
                isDefaultPrint = false,
                widthPaper = paperWidth,
            )
        printerConnection = printerConnectionFactory.create(PrinterConnectionType.USB)
        printerConnection.print(context, printerInfo, PrintContent.Test)
        collectPrinterState()
    }

    private fun collectPrinterState() {
        launchCatching {
            printerConnection.printerState.printerState.collect {
                printerState.value = it
            }
        }
    }
}