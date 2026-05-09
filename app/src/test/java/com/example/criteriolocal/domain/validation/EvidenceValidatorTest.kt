package com.example.criteriolocal.domain.validation

import com.example.criteriolocal.domain.model.EvidenceType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EvidenceValidatorTest {
    @Test
    fun validate_acceptsEmptyEvidenceBecauseItIsOptional() {
        val result = EvidenceValidator.validate(emptyList())

        assertTrue(result.isValid)
    }

    @Test
    fun validate_acceptsImageWithAllowedExtensionAndValidSize() {
        val result = EvidenceValidator.validate(
            listOf(
                EvidenceValidationRequest(
                    filePath = "evidencias/factura-001.jpg",
                    fileType = EvidenceType.IMAGE,
                    fileSizeBytes = 250_000L,
                    uploadedOn = "2026-05-08",
                ),
            ),
        )

        assertTrue(result.isValid)
    }

    @Test
    fun validate_acceptsPdfWithAllowedExtensionAndValidSize() {
        val result = EvidenceValidator.validate(
            listOf(
                EvidenceValidationRequest(
                    filePath = "evidencias/cotizacion-001.pdf",
                    fileType = EvidenceType.PDF,
                    fileSizeBytes = EvidenceFilePolicy.maxFileSizeBytes,
                    uploadedOn = "2026-05-08",
                ),
            ),
        )

        assertTrue(result.isValid)
    }

    @Test
    fun validate_rejectsMissingSize() {
        val result = EvidenceValidator.validate(
            listOf(
                EvidenceValidationRequest(
                    filePath = "evidencias/factura-001.jpg",
                    fileType = EvidenceType.IMAGE,
                    fileSizeBytes = null,
                    uploadedOn = "2026-05-08",
                ),
            ),
        )

        assertFalse(result.isValid)
        assertTrue(result.hasError(ValidationErrorCode.EVIDENCE_FILE_SIZE_REQUIRED))
    }

    @Test
    fun validate_rejectsOversizedFile() {
        val result = EvidenceValidator.validate(
            listOf(
                EvidenceValidationRequest(
                    filePath = "evidencias/factura-001.jpg",
                    fileType = EvidenceType.IMAGE,
                    fileSizeBytes = EvidenceFilePolicy.maxFileSizeBytes + 1,
                    uploadedOn = "2026-05-08",
                ),
            ),
        )

        assertFalse(result.isValid)
        assertTrue(result.hasError(ValidationErrorCode.EVIDENCE_FILE_TOO_LARGE))
    }

    @Test
    fun validate_rejectsImpossibleCalendarDate() {
        val result = EvidenceValidator.validate(
            listOf(
                EvidenceValidationRequest(
                    filePath = "evidencias/factura-001.jpg",
                    fileType = EvidenceType.IMAGE,
                    fileSizeBytes = 100_000L,
                    uploadedOn = "2026-02-31",
                ),
            ),
        )

        assertFalse(result.isValid)
        assertTrue(result.hasError(ValidationErrorCode.EVIDENCE_UPLOAD_DATE_INVALID))
    }

    @Test
    fun validate_rejectsExtensionThatDoesNotMatchSelectedType() {
        val result = EvidenceValidator.validate(
            listOf(
                EvidenceValidationRequest(
                    filePath = "evidencias/factura-001.pdf",
                    fileType = EvidenceType.IMAGE,
                    fileSizeBytes = 100_000L,
                    uploadedOn = "2026-05-08",
                ),
            ),
        )

        assertFalse(result.isValid)
        assertTrue(result.hasError(ValidationErrorCode.EVIDENCE_FILE_EXTENSION_INVALID))
    }
}
