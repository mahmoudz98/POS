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

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.casecode.pos.core.model.data.SyncableEntityType
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Represents a command in a local queue for data that needs to be synchronized or reconciled.
 * This acts as a local "to-do" list for the sync process to handle incoming data changes.
 */
@Entity(tableName = "local_signals")
data class LocalSignalEntity(
    @PrimaryKey
    val id: String,
    val entityType: SyncableEntityType,
    val entityId: String,
    val status: Int = LocalSignalStatus.PENDING,
    val createdAt: Instant = Clock.System.now(),
)

object LocalSignalStatus {
    const val PENDING = 0
    const val PROCESSED = 1
    const val FAILED = 2
}
