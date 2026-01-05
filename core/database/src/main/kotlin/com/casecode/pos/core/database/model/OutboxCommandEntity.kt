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
package com.casecode.pos.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Represents a command to be executed for data synchronization, stored in an outbox table.
 * This entity follows the Outbox Pattern, ensuring that write operations are durable and
 * reliably synchronized with a remote data source in the background.
 *
 * @property id The unique identifier for the command.
 * @property type A string identifying the type of command (e.g., "CREATE_BUSINESS").
 * @property payload A JSON string containing all the data needed to execute the command.
 * @property status The current status of the command (e.g., PENDING, PROCESSING, COMPLETED, FAILED).
 * @property createdAt The timestamp when the command was created.
 * @property attemptCount The number of times this command has been attempted.
 * @property lastAttemptedAt The timestamp of the last attempt to execute this command.
 * @property lastError An optional message describing the reason for the last failure.
 */
@Entity(tableName = "outbox_commands")
data class OutboxCommandEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: OutboxEventType,
    val payload: String,
    val status: Int = OutboxCommandStatus.PENDING,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Clock.System.now(),
    @ColumnInfo(name = "attempt_count")
    val attemptCount: Int = 0,
    @ColumnInfo(name = "last_attempted_at")
    val lastAttemptedAt: Instant? = null,
    @ColumnInfo(name = "last_error")
    val lastError: String? = null,
)
sealed interface OutboxEventType {
    enum class Business : OutboxEventType {
        CREATED,
        UPDATED,
        DELETED,
    }
    enum class Branch : OutboxEventType {
        CREATED,
        UPDATED,
        DELETED,
    }
    enum class Employee : OutboxEventType {
        CREATED,
        UPDATED,
        DELETED,
    }
    enum class ProductO : OutboxEventType {
        CREATED,
        UPDATED,
        DELETED,
    }
    enum class Sale : OutboxEventType {
        COMPLETED,
        REFUNDED,
    }
    enum class Stock : OutboxEventType {
        RECEIVED,
        TRANSFERRED,
        ADJUSTED,
    }
}

/**
 * Defines the possible statuses for an [OutboxCommandEntity].
 */
object OutboxCommandStatus {
    const val PENDING = 0
    const val PROCESSING = 1
    const val COMPLETED = 2
    const val FAILED = 3
}
