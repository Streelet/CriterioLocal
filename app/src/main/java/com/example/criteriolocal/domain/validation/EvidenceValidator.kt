package com.example.criteriolocal.domain.validation

object EvidenceValidator {
    private val isoDatePattern = Regex("""^\d{4}-\d{2}-\d{2}$""")

    fun validate(items: List<EvidenceValidationRequest>): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        items.forEachIndexed { index, item ->
            val prefix = "evidences[$index]"
            if (item.filePath.isNullOrBlank()) {
                errors += ValidationError(
                    code = ValidationErrorCode.EVIDENCE_FILE_PATH_REQUIRED,
                    field = "$prefix.filePath",
                    message = "La evidencia debe incluir ruta de archivo.",
                )
            }
            if (item.fileType == null) {
                errors += ValidationError(
                    code = ValidationErrorCode.EVIDENCE_FILE_TYPE_REQUIRED,
                    field = "$prefix.fileType",
                    message = "La evidencia debe indicar tipo de archivo.",
                )
            }
            validateDate(item.uploadedOn, "$prefix.uploadedOn", errors)
        }

        return ValidationResult(errors)
    }

    private fun validateDate(
        value: String?,
        field: String,
        errors: MutableList<ValidationError>,
    ) {
        when {
            value.isNullOrBlank() -> errors += ValidationError(
                code = ValidationErrorCode.EVIDENCE_UPLOAD_DATE_REQUIRED,
                field = field,
                message = "La evidencia debe incluir fecha de carga.",
            )
            !value.matches(isoDatePattern) || !hasValidDateParts(value) -> {
                errors += ValidationError(
                    code = ValidationErrorCode.EVIDENCE_UPLOAD_DATE_INVALID,
                    field = field,
                    message = "La fecha de evidencia debe usar formato YYYY-MM-DD.",
                )
            }
        }
    }

    private fun hasValidDateParts(value: String): Boolean {
        val parts = value.split("-")
        if (parts.size != 3) return false

        val month = parts[1].toIntOrNull() ?: return false
        val day = parts[2].toIntOrNull() ?: return false

        return month in 1..12 && day in 1..31
    }
}
