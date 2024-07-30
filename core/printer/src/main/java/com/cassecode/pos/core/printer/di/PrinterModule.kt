package com.cassecode.pos.core.printer.di

import com.cassecode.pos.core.printer.BluetoothPrinterConnection
import com.cassecode.pos.core.printer.PrinterConnection
import com.cassecode.pos.core.printer.PrinterConnectionFactory
import com.cassecode.pos.core.printer.TcpPrinterConnection
import com.cassecode.pos.core.printer.UsbPrinterConnection
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object PrinterModule {

    @Provides
    internal fun provideBluetoothPrinterConnection(bluetoothPrinter: PrinterConnection): PrinterConnection =
        BluetoothPrinterConnection()

    @Provides
    internal fun bindTcpPrinterConnection(tcpPrinterConnection: PrinterConnection): PrinterConnection = TcpPrinterConnection()

    @Provides
    internal fun bindUsbPrinterConnection(usbPrinterConnection: PrinterConnection): PrinterConnection =
        UsbPrinterConnection()


    @Provides
    fun providePrinterConnectionFactory(
        bluetoothPrinterConnection: BluetoothPrinterConnection,
        tcpPrinterConnection: TcpPrinterConnection,
        usbPrinterConnection: UsbPrinterConnection,
    ): PrinterConnectionFactory {
        return PrinterConnectionFactory(
            bluetoothPrinterConnection,
            tcpPrinterConnection,
            usbPrinterConnection,
        )
    }


}