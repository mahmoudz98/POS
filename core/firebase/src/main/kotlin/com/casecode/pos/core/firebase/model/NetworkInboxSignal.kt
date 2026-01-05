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
package com.casecode.pos.core.firebase.model

import com.google.firebase.database.Exclude

/**
 * Represents a lightweight sync signal received from the Firebase Realtime Database "inbox".
 * This signal informs the client that a specific entity has been updated on the server.
 *
 * @property entityType A string representing the type of entity that was updated (e.g., "ITEM", "INVOICE").
 * @property entityId The unique identifier of the entity that was updated.
 * @property lastUpdated A server-side timestamp indicating when the update occurred.
 */
data class NetworkInboxSignal(
    @Exclude val id: String = "",
    val entityType: String = "",
    val entityId: String = "",
    val operationType: String = OperationType.UPDATED,
    val lastUpdated: Long = 0L,
)

object OperationType {
    const val CREATED = "created"
    const val UPDATED = "updated"
    const val DELETED = "deleted"
}
enum class EntityType {
    Business,
    Branch,
    TaxRate,
    Employee,
}
