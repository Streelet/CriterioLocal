package com.example.criteriolocal.domain.management

import com.example.criteriolocal.domain.model.AvailabilityOption
import com.example.criteriolocal.domain.model.Rating
import com.example.criteriolocal.domain.model.ServiceModeOption
import com.example.criteriolocal.domain.model.UsageFrequencyOption
import com.example.criteriolocal.domain.model.WaitTimeOption

internal object FakeRatings {
    fun rating(
        id: Long = 1,
        userId: Long = 1,
        businessId: Long = 1,
    ): Rating {
        return Rating(
            id = id,
            userId = userId,
            businessId = businessId,
            ratedOn = "2026-04-29",
            reportedPrice = 50.0,
            priceReportedOn = "2026-04-29",
            serviceScore = 5,
            attentionScore = 5,
            satisfactionScore = 5,
            waitTime = WaitTimeOption.UP_TO_15_MINUTES,
            wouldRecommend = true,
            usageFrequency = UsageFrequencyOption.FIRST_TIME,
            availability = AvailabilityOption.AVAILABLE,
            serviceMode = ServiceModeOption.IN_PERSON,
        )
    }
}
