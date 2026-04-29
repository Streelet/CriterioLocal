package com.example.criteriolocal.domain.catalog

import com.example.criteriolocal.domain.model.AvailabilityOption
import com.example.criteriolocal.domain.model.CatalogOption
import com.example.criteriolocal.domain.model.CatalogStatus
import com.example.criteriolocal.domain.model.Quality
import com.example.criteriolocal.domain.model.ServiceModeOption
import com.example.criteriolocal.domain.model.UsageFrequencyOption
import com.example.criteriolocal.domain.model.WaitTimeOption

object AppCatalogs {
    const val minRatingScore: Int = 1
    const val maxRatingScore: Int = 5
    const val minReportedPrice: Double = 0.01
    const val maxReportedPrice: Double = 100_000.00

    val ratingScale: List<Int> = (minRatingScore..maxRatingScore).toList()

    val waitTimeOptions = listOf(
        CatalogOption(WaitTimeOption.UP_TO_15_MINUTES, "Hasta 15 minutos"),
        CatalogOption(WaitTimeOption.FROM_15_TO_30_MINUTES, "De 15 a 30 minutos"),
        CatalogOption(WaitTimeOption.FROM_31_TO_60_MINUTES, "De 31 a 60 minutos"),
        CatalogOption(WaitTimeOption.MORE_THAN_60_MINUTES, "Mas de 60 minutos"),
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
        CatalogOption(ServiceModeOption.ONLINE, "En linea"),
    )

    val officialQualities = listOf(
        Quality(1, "Atencion rapida", "El servicio se completo en poco tiempo.", null, CatalogStatus.ACTIVE),
        Quality(2, "Trato amable", "La atencion fue cordial y respetuosa.", null, CatalogStatus.ACTIVE),
        Quality(3, "Precio accesible", "El precio reportado fue percibido como competitivo.", null, CatalogStatus.ACTIVE),
        Quality(4, "Higiene adecuada", "Las instalaciones o procesos mostraron limpieza adecuada.", null, CatalogStatus.ACTIVE),
        Quality(5, "Buena explicacion del servicio", "Se explico claramente el producto o procedimiento.", null, CatalogStatus.ACTIVE),
        Quality(6, "Puntualidad", "Se cumplio el tiempo prometido para la atencion o entrega.", null, CatalogStatus.ACTIVE),
        Quality(7, "Variedad de productos", "Se observo surtido suficiente en el negocio.", 2, CatalogStatus.ACTIVE),
        Quality(8, "Servicio confiable", "La experiencia transmitio confianza y seguridad.", null, CatalogStatus.ACTIVE),
        Quality(9, "Diagnostico claro", "La explicacion clinica fue entendible y precisa.", 5, CatalogStatus.ACTIVE),
        Quality(10, "Repuestos disponibles", "El negocio tuvo piezas o insumos necesarios al momento.", 4, CatalogStatus.ACTIVE),
    )

    const val ethicalNotice =
        "La app solo admite valoraciones estructuradas, comparables y sin texto libre."
}
