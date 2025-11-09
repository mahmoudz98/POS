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
package com.casecode.pos.core.data.utils

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PasswordUtilsTest {

    @Test
    fun `hash returns non-empty string`() {
        val password = "myPassword123"
        val hash = PasswordUtils.hashPassword(password)
        assertTrue(hash.isNotEmpty())
    }

    @Test
    fun `hash produces different hash for same password`() {
        val password = "myPassword123"
        val hash1 = PasswordUtils.hashPassword(password)
        val hash2 = PasswordUtils.hashPassword(password)
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun `verify with correct password returns true`() {
        val password = "myPassword123"
        val hash = PasswordUtils.hashPassword(password)
        assertTrue(PasswordUtils.verifyPassword(password, hash))
    }

    @Test
    fun `verify with incorrect password returns false`() {
        val password = "myPassword123"
        val incorrectPassword = "wrongPassword"
        val hash = PasswordUtils.hashPassword(password)
        assertFalse(PasswordUtils.verifyPassword(incorrectPassword, hash))
    }

    @Test
    fun `verify with malformed hash returns false`() {
        val password = "myPassword123"
        val malformedHash = "this:is:not:a:valid:hash"
        assertFalse(PasswordUtils.verifyPassword(password, malformedHash))
    }

    @Test
    fun `verify with empty password returns true if hash is of empty string`() {
        val password = ""
        val hash = PasswordUtils.hashPassword(password)
        assertTrue(PasswordUtils.verifyPassword(password, hash))
    }
}
