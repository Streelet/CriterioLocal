package com.example.criteriolocal.domain.security

import java.security.MessageDigest
import java.nio.charset.StandardCharsets

object PasswordHasher {
    fun sha256(value: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(value.toByteArray(StandardCharsets.UTF_8))
        return bytes.joinToString(separator = "") { "%02x".format(it.toInt() and 0xff) }
    }
}
