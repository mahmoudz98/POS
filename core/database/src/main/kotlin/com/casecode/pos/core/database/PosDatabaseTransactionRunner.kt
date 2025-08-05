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
package com.casecode.pos.core.database

import androidx.room.withTransaction
import com.casecode.pos.core.database.util.DatabaseTransactionRunner
import javax.inject.Inject

/**
 * Implementation of [DatabaseTransactionRunner] that uses [withTransaction] to run transactions.
 */
class PosDatabaseTransactionRunner @Inject constructor(
    private val db: PosDatabase,
) : DatabaseTransactionRunner {
    override suspend fun <R> run(block: suspend () -> R): R {
        return db.withTransaction { block() }
    }
}