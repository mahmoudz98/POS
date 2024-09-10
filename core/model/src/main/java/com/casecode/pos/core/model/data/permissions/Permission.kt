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
package com.casecode.pos.core.model.data.permissions

data class Permission1(
    val description: String,
    val name: String,
)

enum class Permission(
    val englishName: String,
    val arabicName: String,
) {
    ADMIN("Admin", ""),
    SALE("Sales", ""),
    NONE("None", ""),
}

fun String.toPermission(): Permission? =
    Permission.entries.find { type ->
        type.arabicName == this || type.englishName.lowercase() == this.lowercase()
    }