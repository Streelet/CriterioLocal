package com.example.criteriolocal.domain.validation

import com.example.criteriolocal.domain.catalog.AppCatalogs
import com.example.criteriolocal.domain.model.CatalogStatus

object RatingValidator {
    private val isoDatePattern = Regex("""^\d{4}-\d{2}-\d{2}$""")

    fun validate(
        request: RatingValidationRequest,
        context: RatingValidationContext,
    ): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (!request.freeTextComment.isNullOrBlank()) {
            errors += error(
                ValidationErrorCode.FREE_TEXT_NOT_ALLOWED,
                "freeTextComment",
                "Las valoraciones no aceptan comentarios de texto libre.",
            )
        }

        validateUser(request, context, errors)
        val businessCategoryId = validateBusiness(request, context, errors)
        validateDate(request.ratedOn, "ratedOn", ValidationErrorCode.RATED_DATE_REQUIRED, ValidationErrorCode.RATED_DATE_INVALID, errors)
        validatePrice(request, context, errors)
        validateScore(request.serviceScore, "serviceScore", ValidationErrorCode.SERVICE_SCORE_REQUIRED, ValidationErrorCode.SERVICE_SCORE_OUT_OF_RANGE, context, errors)
        validateScore(request.attentionScore, "attentionScore", ValidationErrorCode.ATTENTION_SCORE_REQUIRED, ValidationErrorCode.ATTENTION_SCORE_OUT_OF_RANGE, context, errors)
        validateScore(request.satisfactionScore, "satisfactionScore", ValidationErrorCode.SATISFACTION_SCORE_REQUIRED, ValidationErrorCode.SATISFACTION_SCORE_OUT_OF_RANGE, context, errors)
        validateRequiredOption(request.waitTime, "waitTime", ValidationErrorCode.WAIT_TIME_REQUIRED, errors)
        validateRequiredOption(request.wouldRecommend, "wouldRecommend", ValidationErrorCode.RECOMMENDATION_REQUIRED, errors)
        validateRequiredOption(request.usageFrequency, "usageFrequency", ValidationErrorCode.USAGE_FREQUENCY_REQUIRED, errors)
        validateRequiredOption(request.availability, "availability", ValidationErrorCode.AVAILABILITY_REQUIRED, errors)
        validateRequiredOption(request.serviceMode, "serviceMode", ValidationErrorCode.SERVICE_MODE_REQUIRED, errors)
        validateQualities(request, context, businessCategoryId, errors)

        return ValidationResult(errors)
    }

    private fun validateUser(
        request: RatingValidationRequest,
        context: RatingValidationContext,
        errors: MutableList<ValidationError>,
    ) {
        val userId = request.userId
        when {
            userId == null || userId <= 0L -> {
                errors += error(ValidationErrorCode.USER_REQUIRED, "userId", "La valoracion debe asociarse a un usuario.")
            }
            userId !in context.existingUserIds -> {
                errors += error(ValidationErrorCode.USER_NOT_FOUND, "userId", "El usuario indicado no existe.")
            }
        }
    }

    private fun validateBusiness(
        request: RatingValidationRequest,
        context: RatingValidationContext,
        errors: MutableList<ValidationError>,
    ): Long? {
        val businessId = request.businessId
        return when {
            businessId == null || businessId <= 0L -> {
                errors += error(ValidationErrorCode.BUSINESS_REQUIRED, "businessId", "La valoracion debe asociarse a un negocio.")
                null
            }
            businessId !in context.existingBusinessCategoryIds.keys -> {
                errors += error(ValidationErrorCode.BUSINESS_NOT_FOUND, "businessId", "El negocio indicado no existe.")
                null
            }
            else -> context.existingBusinessCategoryIds[businessId]
        }
    }

    private fun validatePrice(
        request: RatingValidationRequest,
        context: RatingValidationContext,
        errors: MutableList<ValidationError>,
    ) {
        val price = request.reportedPrice
        when {
            price == null -> {
                errors += error(ValidationErrorCode.PRICE_REQUIRED, "reportedPrice", "El precio reportado es obligatorio.")
            }
            price < context.minReportedPrice || price > context.maxReportedPrice -> {
                errors += error(
                    ValidationErrorCode.PRICE_OUT_OF_RANGE,
                    "reportedPrice",
                    "El precio debe estar entre ${context.minReportedPrice} y ${context.maxReportedPrice}.",
                )
            }
        }

        validateDate(
            value = request.priceReportedOn,
            field = "priceReportedOn",
            requiredCode = ValidationErrorCode.PRICE_DATE_REQUIRED,
            invalidCode = ValidationErrorCode.PRICE_DATE_INVALID,
            errors = errors,
        )
    }

    private fun validateScore(
        value: Int?,
        field: String,
        requiredCode: ValidationErrorCode,
        invalidCode: ValidationErrorCode,
        context: RatingValidationContext,
        errors: MutableList<ValidationError>,
    ) {
        when {
            value == null -> errors += error(requiredCode, field, "La calificacion es obligatoria.")
            value !in context.ratingScale -> {
                errors += error(
                    invalidCode,
                    field,
                    "La calificacion debe estar en la escala ${AppCatalogs.minRatingScore} a ${AppCatalogs.maxRatingScore}.",
                )
            }
        }
    }

    private fun validateDate(
        value: String?,
        field: String,
        requiredCode: ValidationErrorCode,
        invalidCode: ValidationErrorCode,
        errors: MutableList<ValidationError>,
    ) {
        when {
            value.isNullOrBlank() -> errors += error(requiredCode, field, "La fecha es obligatoria.")
            !value.matches(isoDatePattern) || !hasValidDateParts(value) -> {
                errors += error(invalidCode, field, "La fecha debe usar formato YYYY-MM-DD.")
            }
        }
    }

    private fun validateRequiredOption(
        value: Any?,
        field: String,
        code: ValidationErrorCode,
        errors: MutableList<ValidationError>,
    ) {
        if (value == null) {
            errors += error(code, field, "La opcion estructurada es obligatoria.")
        }
    }

    private fun validateQualities(
        request: RatingValidationRequest,
        context: RatingValidationContext,
        businessCategoryId: Long?,
        errors: MutableList<ValidationError>,
    ) {
        if (request.selectedQualityIds.isEmpty()) {
            errors += error(ValidationErrorCode.QUALITY_REQUIRED, "selectedQualityIds", "Debe seleccionarse al menos una cualidad del catalogo.")
            return
        }

        val duplicated = request.selectedQualityIds
            .groupingBy { it }
            .eachCount()
            .filterValues { it > 1 }
            .keys

        duplicated.forEach { qualityId ->
            errors += error(ValidationErrorCode.QUALITY_DUPLICATED, "selectedQualityIds", "La cualidad $qualityId esta duplicada.")
        }

        val qualitiesById = context.officialQualities.associateBy { it.id }
        request.selectedQualityIds.distinct().forEach { qualityId ->
            val quality = qualitiesById[qualityId]
            when {
                quality == null -> {
                    errors += error(ValidationErrorCode.QUALITY_NOT_IN_CATALOG, "selectedQualityIds", "La cualidad $qualityId no existe en el catalogo oficial.")
                }
                quality.status != CatalogStatus.ACTIVE -> {
                    errors += error(ValidationErrorCode.QUALITY_INACTIVE, "selectedQualityIds", "La cualidad ${quality.name} no esta activa.")
                }
                businessCategoryId != null && quality.applicableCategoryId != null && quality.applicableCategoryId != businessCategoryId -> {
                    errors += error(
                        ValidationErrorCode.QUALITY_NOT_APPLICABLE_TO_BUSINESS_CATEGORY,
                        "selectedQualityIds",
                        "La cualidad ${quality.name} no aplica para la categoria del negocio.",
                    )
                }
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

    private fun error(
        code: ValidationErrorCode,
        field: String,
        message: String,
    ): ValidationError {
        return ValidationError(code = code, field = field, message = message)
    }
}
