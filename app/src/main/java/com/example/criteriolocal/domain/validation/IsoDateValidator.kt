package com.example.criteriolocal.domain.validation

import java.text.SimpleDateFormat
import java.util.Locale

object IsoDateValidator {
    private val isoDatePattern = Regex("""^\d{4}-\d{2}-\d{2}$""")

    fun isValid(value: String): Boolean {
        if (!value.matches(isoDatePattern)) return false

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            isLenient = false
        }
        val parsed = runCatching { formatter.parse(value) }.getOrNull() ?: return false
        return formatter.format(parsed) == value
    }
}
