package com.example.criteriolocal.domain.contract

import com.example.criteriolocal.domain.metrics.BusinessMetricsSummary
import com.example.criteriolocal.domain.metrics.CategoryRanking
import com.example.criteriolocal.domain.metrics.CategoryRankingItem
import com.example.criteriolocal.domain.metrics.QualitySelectionMetric
import com.example.criteriolocal.domain.model.Business
import com.example.criteriolocal.domain.model.BusinessStatus
import com.example.criteriolocal.domain.model.BusinessWithCategory
import com.example.criteriolocal.domain.model.CatalogStatus
import com.example.criteriolocal.domain.model.Category
import com.example.criteriolocal.domain.model.EvidenceType
import com.example.criteriolocal.domain.model.Quality
import com.example.criteriolocal.domain.model.WaitTimeOption
import com.example.criteriolocal.domain.query.BusinessSearchItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ContractMappersTest {
    @Test
    fun toRatingFormCatalogsDto_exposesOnlyActiveQualitiesAndCatalogCodes() {
        val catalogs = listOf(
            Quality(1, "Atencion rapida", "Rapidez.", null, CatalogStatus.ACTIVE),
            Quality(2, "Inactiva", "No debe aparecer.", null, CatalogStatus.INACTIVE),
        ).toRatingFormCatalogsDto()

        assertEquals((1..5).toList(), catalogs.ratingScale)
        assertEquals(listOf("Atencion rapida"), catalogs.qualities.map { it.name })
        assertTrue(catalogs.waitTimeOptions.any { it.code == WaitTimeOption.UP_TO_15_MINUTES.name })
        assertTrue(catalogs.ethicalNotice.contains("sin texto libre"))
    }

    @Test
    fun ratingRegistrationRequestDto_mapsValidCatalogCodesToDomainRequest() {
        val mapped = validRatingRequestDto().toDomainRequest()

        assertTrue(mapped.isSuccess)
        assertEquals(WaitTimeOption.UP_TO_15_MINUTES, mapped.value?.waitTime)
        assertEquals(EvidenceType.IMAGE, mapped.value?.evidences?.single()?.fileType)
        assertNull(mapped.value?.freeTextComment)
    }

    @Test
    fun ratingRegistrationRequestDto_reportsInvalidCatalogCode() {
        val mapped = validRatingRequestDto(waitTimeCode = "NO_EXISTE").toDomainRequest()

        assertFalse(mapped.isSuccess)
        assertEquals("INVALID_CATALOG_OPTION", mapped.errors.single().code)
        assertEquals("waitTimeCode", mapped.errors.single().field)
    }

    @Test
    fun businessSearchItem_mapsToFrontendListDto() {
        val item = BusinessSearchItem(
            businessWithCategory = businessWithCategory,
            totalRatings = 3,
            averageScore = 4.5,
            minReportedPrice = 20.0,
            maxReportedPrice = 50.0,
        )

        val dto = item.toDto()

        assertEquals("Farmacia Bienestar", dto.business.name)
        assertEquals("Farmacia", dto.business.categoryName)
        assertEquals(3, dto.totalRatings)
        assertEquals(4.5, dto.averageScore ?: 0.0, 0.001)
    }

    @Test
    fun metricsSummaryAndRanking_mapToFrontendDtos() {
        val summary = BusinessMetricsSummary(
            businessWithCategory = businessWithCategory,
            totalRatings = 2,
            averageServiceScore = 4.5,
            averageAttentionScore = 4.0,
            averageSatisfactionScore = 5.0,
            generalScore = 4.5,
            recommendationPercentage = 100.0,
            minReportedPrice = 20.0,
            maxReportedPrice = 40.0,
            averageReportedPrice = 30.0,
            topQualities = listOf(QualitySelectionMetric(quality, selectionCount = 2)),
        )
        val ranking = CategoryRanking(
            category = category,
            items = listOf(CategoryRankingItem(position = 1, summary = summary)),
        )

        val summaryDto = summary.toDto()
        val rankingDto = ranking.toDto()

        assertEquals(4.5, summaryDto.generalScore ?: 0.0, 0.001)
        assertEquals("Atencion rapida", summaryDto.topQualities.single().quality.name)
        assertEquals("Farmacia", rankingDto.categoryName)
        assertEquals(1, rankingDto.items.single().position)
    }

    private fun validRatingRequestDto(
        waitTimeCode: String = "UP_TO_15_MINUTES",
    ): RatingRegistrationRequestDto {
        return RatingRegistrationRequestDto(
            userId = 1,
            businessId = 1,
            ratedOn = "2026-05-08",
            reportedPrice = 25.0,
            priceReportedOn = "2026-05-08",
            serviceScore = 5,
            attentionScore = 4,
            satisfactionScore = 5,
            waitTimeCode = waitTimeCode,
            wouldRecommend = true,
            usageFrequencyCode = "OCCASIONAL",
            availabilityCode = "AVAILABLE",
            serviceModeCode = "IN_PERSON",
            selectedQualityIds = listOf(1),
            evidences = listOf(
                EvidenceRequestDto(
                    filePath = "evidencias/factura.jpg",
                    fileTypeCode = "IMAGE",
                    uploadedOn = "2026-05-08",
                ),
            ),
        )
    }

    private companion object {
        val category = Category(2, "Farmacia", "Venta de medicamentos.")
        val businessWithCategory = BusinessWithCategory(
            business = Business(
                id = 1,
                googlePlaceId = "google-place-id",
                name = "Farmacia Bienestar",
                description = "Negocio importado desde Google Places.",
                address = "9a Avenida, Chiquimula",
                categoryId = 2,
                status = BusinessStatus.ACTIVE,
            ),
            category = category,
        )
        val quality = Quality(1, "Atencion rapida", "Rapidez.", null, CatalogStatus.ACTIVE)
    }
}
