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
package com.casecode.pos.core.model.data

enum class PrinterConnectionType(
    val en: String,
    val ar: String,
) {
    BLUETOOTH("Bluetooth", "بلوتوث"),
    USB("Usb", "Usb"),
    ETHERNET("Ethernet", "إيثرنت"),
}

fun String.toConnectionType(): PrinterConnectionType? =
    PrinterConnectionType.entries.find { type ->
        type.ar == this || type.en.lowercase() == this.lowercase()
    }