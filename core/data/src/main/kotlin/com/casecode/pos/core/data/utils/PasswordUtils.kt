package com.casecode.pos.core.data.utils

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * A utility object for handling password hashing and verification using PBKDF2.
 * This implementation uses only pure Java/Kotlin libraries to be testable on the JVM.
 */
object PasswordUtils {

    private const val SALT_LENGTH = 16
    private const val HASH_LENGTH = 32
    private const val ITERATIONS = 100000 // OWASP recommended minimum
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"

    /**
     * Hash a password using PBKDF2 with SHA256
     * Returns: "salt:hash" format, Base64 encoded.
     */
    fun hashPassword(password: String): String {
        val salt = generateSalt()
        val hash = pbkdf2Hash(password, salt)

        val saltBase64 = Base64.getEncoder().encodeToString(salt)
        val hashBase64 = Base64.getEncoder().encodeToString(hash)

        return "$saltBase64:$hashBase64"
    }

    /**
     * Verify a password against a stored hash in the "salt:hash" format.
     */
    fun verifyPassword(password: String, storedHash: String): Boolean {
        return try {
            val parts = storedHash.split(":")
            if (parts.size != 2) return false

            val salt = Base64.getDecoder().decode(parts[0])
            val originalHash = Base64.getDecoder().decode(parts[1])

            val testHash = pbkdf2Hash(password, salt)

            constantTimeEquals(originalHash, testHash)
        } catch (e: Exception) {
            // Any error during decoding or hashing means verification fails.
            false
        }
    }

    private fun generateSalt(): ByteArray {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)
        return salt
    }

    private fun pbkdf2Hash(password: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            ITERATIONS,
            HASH_LENGTH * 8 // Convert to bits
        )
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        return factory.generateSecret(spec).encoded
    }

    private fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false

        var result = 0
        for (i in a.indices) {
            result = result or (a[i].toInt() xor b[i].toInt())
        }
        return result == 0
    }
}