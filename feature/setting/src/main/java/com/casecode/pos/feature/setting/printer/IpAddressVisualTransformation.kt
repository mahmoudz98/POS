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
package com.casecode.pos.feature.setting.printer

import com.casecode.pos.feature.setting.R

fun formatIPAddress(input: String): String {
    val cleanedInput = input.filter { it.isDigit() || it == '.' }
    val segments = cleanedInput.split('.')
    val formattedSegments =
        segments.map { segment ->
            if (segment.length > 3) segment.take(3) else segment
        }

    return formattedSegments.joinToString(".")
}

fun validateIPAddress(ip: String): Int? {
    if (ip.isBlank()) return R.string.feature_setting_printer_info_error_ethernet_ip_address_empty
    val segments = ip.split('.')
    if (segments.size > 4 || segments.size < 4) {
        return R.string.feature_setting_printer_info_error_ethernet_ip_address_invalid
    }

    segments.forEach { segment ->
        if (segment.isNotEmpty() && ((segment.toIntOrNull() ?: -1) !in 0..255)) {
            return R.string.feature_setting_printer_info_error_ethernet_ip_address_invalid_segment
        }
    }

    return null
}

fun validatePort(port: String): Int? {
    val portNumber =
        port.toIntOrNull() ?: R.string.feature_setting_printer_info_error_ethernet_port_empty
    return portNumber
        .takeUnless { it in 1..65535 }
        ?.let { R.string.feature_setting_printer_info_error_ethernet_port_invalid }
}