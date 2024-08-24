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
    ): PrinterConnectionFactory =
        PrinterConnectionFactory(
            bluetoothPrinterConnection,
            tcpPrinterConnection,
            usbPrinterConnection,
        )
}