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
package com.casecode.pos.core.testing.util

import com.casecode.pos.core.database.util.DatabaseTransactionRunner

/**
 * A fake implementation of [DatabaseTransactionRunner] for use in tests.
 * It does not perform any real database transaction but simply executes the
 * passed block of code immediately.
 */
class TestDatabaseTransactionRunner : DatabaseTransactionRunner {
    private var isError = false
    override suspend fun <T> run(block: suspend () -> T): T {
        if (isError) throw RuntimeException("Test database transaction error")
        return block()
    }
    fun setError() {
        isError = true
    }
}