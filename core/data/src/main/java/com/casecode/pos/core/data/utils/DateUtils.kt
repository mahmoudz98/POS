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
package com.casecode.pos.core.data.utils

import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date.toFormattedDateTimeString(): String =
    this.let {
        SimpleDateFormat("MMM dd, yyyy hh:mm a ", Locale.getDefault()).format(this)
    }

fun Date.toDateFormatString(): String {
    val dataFormated =
        this
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("MM, dd, yyyy"))

    return dataFormated
}

fun Date.toTimeFormatedString(): String =
    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(this)

fun Calendar.toDateFormatString(): String {
    val year = this.get(Calendar.YEAR)
    val month = this.get(Calendar.MONTH) + 1
    val dayOfMonth = this.get(Calendar.DAY_OF_MONTH)
    val dataFormated =
        String.format(
            Locale.getDefault(),
            "%s %02d ,%0d",
            year,
            month,
            dayOfMonth,
        )
    return dataFormated
}