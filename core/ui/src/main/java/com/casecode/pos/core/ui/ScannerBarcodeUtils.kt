package com.casecode.pos.core.ui

import android.content.Context
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import timber.log.Timber

fun Context.scanOptions(
    onResult: (String) -> Unit,
    onFailure: (Int) -> Unit,
    onCancel: (Int) -> Unit,
) {
    val scanner = GmsBarcodeScanning.getClient(this)
    val moduleInstallRequest =
        ModuleInstallRequest
            .newBuilder()
            .addApi(scanner) // Add the scanner client to the module install request
            .build()

    val moduleInstallClient = ModuleInstall.getClient(this)
    moduleInstallClient
        .installModules(moduleInstallRequest)
        .addOnSuccessListener {
            Timber.e("success")
        }.addOnFailureListener {
            onFailure(R.string.core_ui_message_scan_error_open)
            Timber.e("failure: $it")
        }

    scanner
        .startScan()
        .addOnSuccessListener { result ->
            val barcode = result.rawValue
            if (barcode.isNullOrEmpty()) {
                onFailure(R.string.core_ui_scan_result_empty)
            } else {
                onResult(barcode)
            }
        }.addOnFailureListener {
            Timber.e("failure: $it")
            onFailure(R.string.core_ui_scan_result_empty)
        }.addOnCanceledListener {
            onCancel(R.string.core_ui_scan_result_empty)
        }
}