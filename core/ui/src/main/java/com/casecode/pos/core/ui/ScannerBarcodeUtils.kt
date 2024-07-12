package com.casecode.pos.core.ui

import android.content.Context
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanOptions

fun ScanOptions.startScanningBarcode(context: Context, messageId:Int  ): ScanOptions {

    setPrompt(context.getString(messageId))
    setBeepEnabled(false)
    setBarcodeImageEnabled(true)
    setOrientationLocked(true)
    captureActivity = CaptureScannerActivity::class.java
    return this
}
class CaptureScannerActivity : CaptureActivity()