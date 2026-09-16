package com.example.util

import java.security.MessageDigest

object SecurityUtils {
    /**
     * Hashes password using SHA-256 for secure offline credential storage and verification.
     */
    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(inputPassword: String, storedHash: String): Boolean {
        return hashPassword(inputPassword).equals(storedHash, ignoreCase = true)
    }
}
