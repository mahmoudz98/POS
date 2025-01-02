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
package com.casecode.pos.core.printer.di

import com.casecode.pos.core.printer.PrinterConnectionFactory
import com.casecode.pos.core.printer.base.BluetoothEscPosPrint
import com.casecode.pos.core.printer.base.EscPosPrint
import com.casecode.pos.core.printer.base.PrinterStateManager
import com.casecode.pos.core.printer.base.TcpEscPosPrint
import com.casecode.pos.core.printer.base.UsbEscPosPrint
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object PrinterModule {
    @Provides
    @ViewModelScoped
    fun providePrinterStateManager(): PrinterStateManager = PrinterStateManager()

    @Provides
    fun provideBluetoothPrinterConnection(): EscPosPrint = BluetoothEscPosPrint()

    @Provides
    fun provideTcpPrinterConnection(): EscPosPrint = TcpEscPosPrint()

    @Provides
    fun provideUsbPrinterConnection(): EscPosPrint = UsbEscPosPrint()

    @Provides
    @ViewModelScoped
    fun providePrinterConnectionFactory(
        bluetoothPrinterConnection: BluetoothEscPosPrint,
        tcpPrinterConnection: TcpEscPosPrint,
        usbPrinterConnection: UsbEscPosPrint,
    ): PrinterConnectionFactory = PrinterConnectionFactory(
        bluetoothPrinterConnection,
        tcpPrinterConnection,
        usbPrinterConnection,
    )
}