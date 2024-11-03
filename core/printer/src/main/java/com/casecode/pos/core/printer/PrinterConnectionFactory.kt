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
package com.casecode.pos.core.printer

import com.casecode.pos.core.model.data.PrinterConnectionType
import com.casecode.pos.core.printer.base.BluetoothEscPosPrint
import com.casecode.pos.core.printer.base.EscPosPrint
import com.casecode.pos.core.printer.base.TcpEscPosPrint
import com.casecode.pos.core.printer.base.UsbEscPosPrint
import javax.inject.Inject

class PrinterConnectionFactory
@Inject
constructor(
    private val bluetoothEscPosPrint: BluetoothEscPosPrint,
    private val tcpEscPosPrint: TcpEscPosPrint,
    private val usbEscPosPrint: UsbEscPosPrint,
) {
    fun create(printerType: PrinterConnectionType): EscPosPrint = when (printerType) {
        PrinterConnectionType.BLUETOOTH -> bluetoothEscPosPrint
        PrinterConnectionType.ETHERNET -> tcpEscPosPrint
        PrinterConnectionType.USB -> usbEscPosPrint
    }
}