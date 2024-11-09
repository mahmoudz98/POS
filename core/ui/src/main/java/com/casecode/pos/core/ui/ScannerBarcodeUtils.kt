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
package com.casecode.pos.core.ui

import android.content.Context
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import timber.log.Timber

fun Context.scanOptions(
    onResult: (String) -> Unit,
    onFailure: (Int) -> Unit,
    onCancel: (Int) -> Unit,
    onModuleDownloading: (Int) -> Unit,
    onModuleDownloaded: (Int) -> Unit,
) {
    val scanner = GmsBarcodeScanning.getClient(this)
    val moduleInstallRequest =
        ModuleInstallRequest
            .newBuilder()
            .addApi(scanner)
            .build()
    val moduleInstallClient = ModuleInstall.getClient(this)

    moduleInstallClient.areModulesAvailable(scanner).addOnSuccessListener { areModulesAvailable ->
        if (areModulesAvailable.areModulesAvailable()) {
            startScanner(scanner, onResult, onFailure, onCancel)
        } else {
            onModuleDownloading(R.string.core_ui_scan_module_downloading)
            moduleInstallClient
                .installModules(moduleInstallRequest)
                .addOnSuccessListener {
                    onModuleDownloaded(R.string.core_ui_scan_module_complete_downloading)
                    startScanner(scanner, onResult, onFailure, onCancel)
                }.addOnFailureListener { exception ->
                    if (exception is ApiException && exception.statusCode == CommonStatusCodes.NETWORK_ERROR) {
                        onFailure(R.string.core_ui_scan_module_downloading_network_error)
                    } else {
                        onFailure(R.string.core_ui_message_scan_error_open)
                    }
                    Timber.e("ModuleInstallClient failure: $exception")
                }
        }
    }
}
 fun startScanner(
    scanner: GmsBarcodeScanner,
    onResult: (String) -> Unit,
    onFailure: (Int) -> Unit,
    onCancel: (Int) -> Unit,
) {
     scanner
         .startScan()
         .addOnSuccessListener { result ->
             val barcode = result.rawValue
             if (barcode.isNullOrEmpty()) {
                 onFailure(R.string.core_ui_scan_result_empty)
             } else {
                 onResult(barcode)
             }
         }.addOnFailureListener { exception ->
             Timber.e("Scan failure: $exception")
             onFailure(R.string.core_ui_scan_result_empty)
         }.addOnCanceledListener {
             onCancel(R.string.core_ui_scan_result_empty)
         }
 }
