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

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

/**
 * Represents a single entry in the Realtime Database changelog.
 *
 * @property id The unique ID of the document that was changed in Firestore.
 * @property changeType A string representing the type of change (e.g., "CREATED", "UPDATED", "DELETED").
 * @property updatedAt The timestamp of when the change occurred.
 */
data class NetworkChangeList(
    val id: String = "",
    val changeType: String = "",
    @ServerTimestamp val updatedAt: Timestamp = Timestamp.now(),
) {
    /**
     * A convenience property to check if the change was a deletion.
     */
    val isDelete: Boolean
        get() = changeType == "DELETED"
}