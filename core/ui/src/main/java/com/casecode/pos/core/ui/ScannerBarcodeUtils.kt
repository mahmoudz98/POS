package com.casecode.pos.core.ui

import android.content.Context
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning


fun Context.scanOptions(
    onResult: (String) -> Unit,
    onFailure: (Int) -> Unit,
    onCancel: (Int) -> Unit,
) {
    GmsBarcodeScanning.getClient(this).startScan().addOnSuccessListener { result ->
        val barcode = result.rawValue
        if (barcode.isNullOrEmpty()) {
            onFailure(R.string.core_ui_scan_result_empty)
        } else {
            onResult(barcode)
        }
    }.addOnCanceledListener {
        onFailure(R.string.core_ui_scan_result_empty)
    }.addOnCanceledListener {
        onCancel(R.string.core_ui_scan_result_empty)
    }

}