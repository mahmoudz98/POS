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
package com.casecode.pos.core.domain.utils

/**
 * An interface for repositories that manage data requiring two-way synchronization.
 * It defines two distinct operations: pushing local changes up and pulling remote changes down.
 */
interface Syncable {
    /**
     * Pushes pending local changes (from an outbox or similar mechanism) to the remote data source.
     * This is the 'sync up' or 'push' operation.
     *
     * @return `true` if the push was successful, `false` otherwise.
     */
    suspend fun syncUp(): Boolean

    /**
     * Fetches the latest authoritative data from the remote source and reconciles it
     * with the local database. This is the 'sync down' or 'pull' operation.
     * This is typically triggered after receiving a signal that remote data has changed.
     *
     * @return `true` if the pull and reconciliation was successful, `false` otherwise.
     */
    suspend fun syncDown(): Boolean
}