package com.example.criteriolocal.domain.catalog

import com.example.criteriolocal.domain.model.AvailabilityOption
import com.example.criteriolocal.domain.model.CatalogOption
import com.example.criteriolocal.domain.model.ServiceModeOption
import com.example.criteriolocal.domain.model.UsageFrequencyOption
import com.example.criteriolocal.domain.model.WaitTimeOption

object AppCatalogs {
    val ratingScale: List<Int> = (1..5).toList()

    val waitTimeOptions = listOf(
        CatalogOption(WaitTimeOption.UP_TO_15_MINUTES, "Hasta 15 minutos"),
        CatalogOption(WaitTimeOption.FROM_15_TO_30_MINUTES, "De 15 a 30 minutos"),
        CatalogOption(WaitTimeOption.FROM_31_TO_60_MINUTES, "De 31 a 60 minutos"),
        CatalogOption(WaitTimeOption.MORE_THAN_60_MINUTES, "Más de 60 minutos"),
    )

    val usageFrequencyOptions = listOf(
        CatalogOption(UsageFrequencyOption.FIRST_TIME, "Primera vez"),
        CatalogOption(UsageFrequencyOption.OCCASIONAL, "Uso ocasional"),
        CatalogOption(UsageFrequencyOption.FREQUENT, "Uso frecuente"),
        CatalogOption(UsageFrequencyOption.REGULAR, "Uso regular"),
    )

    val availabilityOptions = listOf(
        CatalogOption(AvailabilityOption.NOT_AVAILABLE, "No disponible"),
        CatalogOption(AvailabilityOption.LIMITED, "Disponibilidad limitada"),
        CatalogOption(AvailabilityOption.AVAILABLE, "Disponible"),
    )

    val serviceModeOptions = listOf(
        CatalogOption(ServiceModeOption.IN_PERSON, "Presencial"),
        CatalogOption(ServiceModeOption.HOME_VISIT, "Visita a domicilio"),
        CatalogOption(ServiceModeOption.DELIVERY, "Entrega"),
        CatalogOption(ServiceModeOption.ONLINE, "En línea"),
    )

    const val ethicalNotice =
        "La app solo admite valoraciones estructuradas, comparables y sin texto libre."
}
