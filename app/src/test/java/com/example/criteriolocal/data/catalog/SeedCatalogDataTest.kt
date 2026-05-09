package com.example.criteriolocal.data.catalog

import org.junit.Assert.assertTrue
import org.junit.Test

class SeedCatalogDataTest {
    @Test
    fun ratingsReferenceExistingUsersAndBusinesses() {
        val userIds = SeedCatalogData.users.map { it.id }.toSet()
        val businessIds = SeedCatalogData.businesses.map { it.id }.toSet()

        assertTrue(SeedCatalogData.ratings.all { it.userId in userIds })
        assertTrue(SeedCatalogData.ratings.all { it.businessId in businessIds })
    }

    @Test
    fun ratingQualitiesReferenceExistingRatingsAndQualities() {
        val ratingIds = SeedCatalogData.ratings.map { it.id }.toSet()
        val qualityIds = SeedCatalogData.qualities.map { it.id }.toSet()

        assertTrue(SeedCatalogData.ratingQualities.all { it.ratingId in ratingIds })
        assertTrue(SeedCatalogData.ratingQualities.all { it.qualityId in qualityIds })
    }

    @Test
    fun evidencesReferenceExistingRatings() {
        val ratingIds = SeedCatalogData.ratings.map { it.id }.toSet()

        assertTrue(SeedCatalogData.evidences.all { it.ratingId in ratingIds })
    }
}
