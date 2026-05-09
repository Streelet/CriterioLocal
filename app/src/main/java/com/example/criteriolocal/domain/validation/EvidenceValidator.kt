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
            validateSize(item.fileSizeBytes, "$prefix.fileSizeBytes", errors)
            validateExtension(item, "$prefix.filePath", errors)
            validateDate(item.uploadedOn, "$prefix.uploadedOn", errors)
        }

        return ValidationResult(errors)
    }

    private fun validateSize(
        value: Long?,
        field: String,
        errors: MutableList<ValidationError>,
    ) {
        when {
            value == null -> errors += ValidationError(
                code = ValidationErrorCode.EVIDENCE_FILE_SIZE_REQUIRED,
                field = field,
                message = "La evidencia debe incluir tamano de archivo.",
            )
            value <= 0 -> errors += ValidationError(
                code = ValidationErrorCode.EVIDENCE_FILE_SIZE_INVALID,
                field = field,
                message = "El tamano de evidencia debe ser mayor que cero.",
            )
            value > EvidenceFilePolicy.maxFileSizeBytes -> errors += ValidationError(
                code = ValidationErrorCode.EVIDENCE_FILE_TOO_LARGE,
                field = field,
                message = "La evidencia no debe superar 5 MB.",
            )
        }
    }

    private fun validateExtension(
        item: EvidenceValidationRequest,
        field: String,
        errors: MutableList<ValidationError>,
    ) {
        val filePath = item.filePath?.takeIf { it.isNotBlank() } ?: return
        val fileType = item.fileType ?: return
        val extension = EvidenceFilePolicy.extensionOf(filePath)
        val allowedExtensions = EvidenceFilePolicy.allowedExtensionsFor(fileType)

        if (extension == null || extension !in allowedExtensions) {
            errors += ValidationError(
                code = ValidationErrorCode.EVIDENCE_FILE_EXTENSION_INVALID,
                field = field,
                message = "La extension de evidencia no coincide con el tipo seleccionado.",
            )
        }
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
