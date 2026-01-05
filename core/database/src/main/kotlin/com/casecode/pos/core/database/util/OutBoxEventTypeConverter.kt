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
package com.casecode.pos.core.database.util

import androidx.room.TypeConverter
import com.casecode.pos.core.database.model.OutboxEventType

internal class OutBoxEventTypeConverter {
    @TypeConverter
    fun stringToOutboxEventType(value: String): OutboxEventType {
        val parts = value.split("_")
        val enumClassName = parts[0]
        val enumValueName = parts.subList(1, parts.size).joinToString("_")
        return when (enumClassName) {
            "Business" -> OutboxEventType.Business.valueOf(enumValueName)
            "Branch" -> OutboxEventType.Branch.valueOf(enumValueName)
            "Employee" -> OutboxEventType.Employee.valueOf(enumValueName)
            "ProductO" -> OutboxEventType.ProductO.valueOf(enumValueName)
            "Sale" -> OutboxEventType.Sale.valueOf(enumValueName)
            "Stock" -> OutboxEventType.Stock.valueOf(enumValueName)
            else -> throw IllegalArgumentException("Unknown OutboxEventType: $value")
        }
    }

    @TypeConverter
    fun outboxEventTypeToString(value: OutboxEventType): String = when (value) {
        is OutboxEventType.Business -> "Business_${value.name}"
        is OutboxEventType.Branch -> "Branch_${value.name}"
        is OutboxEventType.Employee -> "Employee_${value.name}"
        is OutboxEventType.ProductO -> "ProductO_${value.name}"
        is OutboxEventType.Sale -> "Sale_${value.name}"
        is OutboxEventType.Stock -> "Stock_${value.name}"
    }
}
