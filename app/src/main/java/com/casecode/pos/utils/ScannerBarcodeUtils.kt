package com.casecode.pos.utils

import android.content.Context
import com.casecode.pos.R
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanOptions

fun ScanOptions.startScanningBarcode(context: Context): ScanOptions {

    setPrompt(context.getString(R.string.scan_barcode))
    setBeepEnabled(false)
    setBarcodeImageEnabled(true)
    setOrientationLocked(true)
    captureActivity = CaptureScannerActivity::class.java
    return this
}
class CaptureScannerActivity : CaptureActivity()