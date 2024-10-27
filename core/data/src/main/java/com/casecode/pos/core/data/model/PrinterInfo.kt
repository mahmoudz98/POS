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
@file:Suppress("UNCHECKED_CAST")

package com.casecode.pos.core.data.model

import com.casecode.pos.core.firebase.services.PRINTER_INFO_ADDRESS_FIELD
import com.casecode.pos.core.firebase.services.PRINTER_INFO_CONNECTION_TYPE_FIELD
import com.casecode.pos.core.firebase.services.PRINTER_INFO_IS_CURRENT_SELECTED_FIELD
import com.casecode.pos.core.firebase.services.PRINTER_INFO_NAME_DEVICE_FIELD
import com.casecode.pos.core.firebase.services.PRINTER_INFO_NAME_FIELD
import com.casecode.pos.core.firebase.services.PRINTER_INFO_PORT_FIELD
import com.casecode.pos.core.firebase.services.PRINTER_INFO_SIZE_FIELD
import com.casecode.pos.core.firebase.services.PRINTER_INFO_USB_NAME_DEVICE_FIELD
import com.casecode.pos.core.model.data.PrinterConnectionInfo
import com.casecode.pos.core.model.data.PrinterInfo
import com.google.firebase.firestore.DocumentSnapshot

fun PrinterInfo.asExternalMapper(): Map<String, Any?> =
    mapOf(
        PRINTER_INFO_NAME_FIELD to this.name,
        PRINTER_INFO_CONNECTION_TYPE_FIELD to this.connectionTypeInfo.asExternalMapper(),
        PRINTER_INFO_IS_CURRENT_SELECTED_FIELD to this.isDefaultPrint,
        PRINTER_INFO_SIZE_FIELD to this.widthPaper,
    )

private fun PrinterConnectionInfo.asExternalMapper(): Map<String, Any?> =
    when (this) {
        is PrinterConnectionInfo.Bluetooth -> {
            mapOf(
                PRINTER_INFO_NAME_DEVICE_FIELD to this.name,
                PRINTER_INFO_ADDRESS_FIELD to this.macAddress,
            )
        }

        is PrinterConnectionInfo.Tcp -> {
            mapOf(
                PRINTER_INFO_ADDRESS_FIELD to this.ipAddress,
                PRINTER_INFO_PORT_FIELD to this.port,
            )
        }

        is PrinterConnectionInfo.Usb -> {
            mapOf(
                PRINTER_INFO_USB_NAME_DEVICE_FIELD to this.usbDeviceName,
            )
        }
    }

fun DocumentSnapshot.asExternalModel(): PrinterInfo = PrinterInfo(
    name = this[PRINTER_INFO_NAME_FIELD] as String,
    connectionTypeInfo = (this[PRINTER_INFO_CONNECTION_TYPE_FIELD] as Map<String, Any?>).asExternalConnectionModel(),
    isDefaultPrint = this[PRINTER_INFO_IS_CURRENT_SELECTED_FIELD] as Boolean,
    widthPaper = this[PRINTER_INFO_SIZE_FIELD] as String,
)

private fun Map<String, Any?>.asExternalConnectionModel(): PrinterConnectionInfo {
    if (this[PRINTER_INFO_USB_NAME_DEVICE_FIELD] != null) {
        return PrinterConnectionInfo.Usb(this[PRINTER_INFO_USB_NAME_DEVICE_FIELD].toString())
    }
    if (this[PRINTER_INFO_ADDRESS_FIELD] != null) {
        return PrinterConnectionInfo.Bluetooth(
            this[PRINTER_INFO_NAME_DEVICE_FIELD].toString(),
            this[PRINTER_INFO_ADDRESS_FIELD].toString(),
        )
    }
    return PrinterConnectionInfo.Tcp(
        this[PRINTER_INFO_ADDRESS_FIELD].toString(),
        this[PRINTER_INFO_PORT_FIELD] as Int,
    )
}