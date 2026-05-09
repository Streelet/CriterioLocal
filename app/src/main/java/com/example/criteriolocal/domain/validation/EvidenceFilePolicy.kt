package com.example.criteriolocal.domain.validation

import com.example.criteriolocal.domain.model.EvidenceType

object EvidenceFilePolicy {
    const val maxFileSizeBytes: Long = 5L * 1024L * 1024L

    val allowedExtensionsByType: Map<EvidenceType, Set<String>> = mapOf(
        EvidenceType.IMAGE to setOf("jpg", "jpeg", "png"),
        EvidenceType.PDF to setOf("pdf"),
    )

    fun allowedExtensionsFor(fileType: EvidenceType): Set<String> {
        return allowedExtensionsByType[fileType].orEmpty()
    }

    fun extensionOf(filePath: String): String? {
        val fileName = filePath
            .substringBefore("?")
            .trim()
            .substringAfterLast('/')
            .substringAfterLast('\\')
        val extension = fileName.substringAfterLast('.', missingDelimiterValue = "")
        return extension.lowercase().takeIf { it.isNotBlank() && extension != fileName }
    }
}
