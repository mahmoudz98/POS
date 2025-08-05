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

/**
 * Type converter for [List] of [String] to allow it to be stored in a Room database.
 */
internal class StringListConverter {
    @TypeConverter
    fun fromString(stringListString: String): List<String> {
        return stringListString.split(",").map { it }
    }

    @TypeConverter
    fun toString(stringList: List<String>): String {
        return stringList.joinToString(separator = ",")
    }
}
internal class OutboxEventTypeConverter {
    @TypeConverter
    fun fromString(value: OutboxEventType): Int {
        return 1
    }

    @TypeConverter
    fun toString(stringList: Int): OutboxEventType {
        return OutboxEventType.BRANCH_CREATED
    }
}