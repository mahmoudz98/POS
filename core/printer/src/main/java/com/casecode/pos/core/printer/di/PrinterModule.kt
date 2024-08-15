package com.casecode.pos.core.printer.di

import com.casecode.pos.core.printer.PrinterConnectionFactory
import com.casecode.pos.core.printer.base.BluetoothEscPosPrint
import com.casecode.pos.core.printer.base.EscPosPrint
import com.casecode.pos.core.printer.base.TcpEscPosPrint
import com.casecode.pos.core.printer.base.UsbEscPosPrint
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object PrinterModule {

    @Provides
    internal fun provideBluetoothPrinterConnection(escPosPrint: EscPosPrint): EscPosPrint =
        BluetoothEscPosPrint()

    @Provides
    internal fun bindTcpPrinterConnection(escPosPrint: EscPosPrint): EscPosPrint = TcpEscPosPrint()

    @Provides
    internal fun bindUsbPrinterConnection(escPosPrint: EscPosPrint): EscPosPrint = UsbEscPosPrint()


    @Provides
    fun providePrinterConnectionFactory(
        bluetoothPrinterConnection: BluetoothEscPosPrint,
        tcpPrinterConnection: TcpEscPosPrint,
        usbPrinterConnection: UsbEscPosPrint,
    ): PrinterConnectionFactory {
        return PrinterConnectionFactory(
            bluetoothPrinterConnection,
            tcpPrinterConnection,
            usbPrinterConnection,
        )
    }
}