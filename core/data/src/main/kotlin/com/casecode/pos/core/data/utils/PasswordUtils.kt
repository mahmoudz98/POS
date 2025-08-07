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

/*
import at.favre.lib.crypto.bcrypt.BCrypt // Import from the new library
import com.casecode.pos.domain.service.LogService // Assuming you can inject a LogService here or handle logging differently
import kotlin.nio.charset.StandardCharsets

*/
/**
 * A utility object for handling password hashing and verification using the modern
 * at.favre.lib:bcrypt library.
 */
/*

object PasswordUtils {

    // Get an instance of the BCrypt hasher. You can configure the version if needed.
    // BCrypt.Version.VERSION_2A is a strong and common choice.
    private val bcrypt = BCrypt.with(BCrypt.Version.VERSION_2A)

    // The cost factor determines how much CPU time is used. 12 is a very strong default.
    private const val COST_FACTOR = 12

 */
/**
 * Hashes a plaintext password using BCrypt with a cost factor of 12.
 *
 * @param password The plaintext password to hash.
 * @return A String containing the complete BCrypt hash (includes version, cost, salt, and hash).
 */
/*

    fun hashPassword(password: String): String {
        // The .withSalt() call automatically generates a secure, random salt for each hash.
        // It's crucial not to reuse salts. This library handles it for you.
        return bcrypt.hashToString(COST_FACTOR, password.toCharArray())
    }

 */

/**
 * Verifies a plaintext password against a stored BCrypt hash.
 *
 * @param password The plaintext password entered by the user.
 * @param storedHash The complete hash string retrieved from the database.
 * @return `true` if the password matches the hash, `false` otherwise.
 */
/*

    fun verifyPassword(password: String, storedHash: String): Boolean {
        return try {
            // The .verify() method automatically extracts the salt and version
            // from the storedHash and performs the comparison.
            val result = BCrypt.verifyer().verify(password.toCharArray(), storedHash)
            result.verified
        } catch (e: Exception) {
            // This can happen if the storedHash is not a valid BCrypt hash string.
            // Log this as a serious error in a real app.
            // logService.logError(e, "BCrypt verification failed due to malformed hash.")
            false
        }
    }
}*/
object PasswordUtils {
    fun pass() = "10"
}
