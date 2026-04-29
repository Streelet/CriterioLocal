package com.example.criteriolocal.domain.catalog

import com.example.criteriolocal.domain.model.AvailabilityOption
import com.example.criteriolocal.domain.model.ServiceModeOption
import com.example.criteriolocal.domain.model.UsageFrequencyOption
import com.example.criteriolocal.domain.model.WaitTimeOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppCatalogsTest {
    @Test
    fun ratingScale_isClosedFromOneToFive() {
        assertEquals(listOf(1, 2, 3, 4, 5), AppCatalogs.ratingScale)
    }

    @Test
    fun structuredOptionCatalogs_coverAllEnumValues() {
        assertEquals(WaitTimeOption.entries.toSet(), AppCatalogs.waitTimeOptions.map { it.value }.toSet())
        assertEquals(UsageFrequencyOption.entries.toSet(), AppCatalogs.usageFrequencyOptions.map { it.value }.toSet())
        assertEquals(AvailabilityOption.entries.toSet(), AppCatalogs.availabilityOptions.map { it.value }.toSet())
        assertEquals(ServiceModeOption.entries.toSet(), AppCatalogs.serviceModeOptions.map { it.value }.toSet())
    }

    @Test
    fun officialQualities_areUniqueAndStructured() {
        val qualities = AppCatalogs.officialQualities

        assertTrue(qualities.isNotEmpty())
        assertEquals(qualities.size, qualities.map { it.id }.toSet().size)
        assertTrue(qualities.all { it.id > 0 })
        assertTrue(qualities.all { it.name.isNotBlank() })
        assertTrue(qualities.all { it.description.isNotBlank() })
    }
}
