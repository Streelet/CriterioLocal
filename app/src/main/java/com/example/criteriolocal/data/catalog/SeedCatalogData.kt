package com.example.criteriolocal.data.catalog

import com.example.criteriolocal.domain.catalog.AppCatalogs
import com.example.criteriolocal.domain.model.AvailabilityOption
import com.example.criteriolocal.domain.model.Business
import com.example.criteriolocal.domain.model.BusinessStatus
import com.example.criteriolocal.domain.model.Category
import com.example.criteriolocal.domain.model.Evidence
import com.example.criteriolocal.domain.model.EvidenceType
import com.example.criteriolocal.domain.model.Rating
import com.example.criteriolocal.domain.model.RatingQuality
import com.example.criteriolocal.domain.model.ServiceModeOption
import com.example.criteriolocal.domain.model.UsageFrequencyOption
import com.example.criteriolocal.domain.model.User
import com.example.criteriolocal.domain.model.UserStatus
import com.example.criteriolocal.domain.model.WaitTimeOption

object SeedCatalogData {
    val categories = listOf(
        Category(1, "Laboratorio clinico", "Servicios de analisis y pruebas diagnosticas."),
        Category(2, "Farmacia", "Venta de medicamentos y productos de cuidado personal."),
        Category(3, "Restaurante", "Negocios de alimentos preparados y atencion en mesa o para llevar."),
        Category(4, "Taller mecanico", "Servicios de mantenimiento y reparacion vehicular."),
        Category(5, "Clinica medica", "Consultas, atencion medica general y especialidades."),
        Category(6, "Tienda local", "Comercios de conveniencia y productos del dia a dia."),
    )

    val qualities = AppCatalogs.officialQualities

    val users = listOf(
        User(
            id = 1,
            name = "Administrador Comunitario",
            email = "admin@criteriolocal.local",
            passwordHash = "hash_admin_demo",
            registeredOn = "2026-04-22",
            status = UserStatus.ACTIVE,
        ),
        User(
            id = 2,
            name = "Usuario Piloto",
            email = "piloto@criteriolocal.local",
            passwordHash = "hash_piloto_demo",
            registeredOn = "2026-04-22",
            status = UserStatus.ACTIVE,
        ),
    )

    val businesses = listOf(
        Business(
            id = 1,
            name = "Laboratorio Vida",
            description = "Laboratorio de analisis clinicos con atencion ambulatoria.",
            address = "4a Avenida 12-45, Zona Centro",
            phone = "5550-0101",
            latitude = 14.7924,
            longitude = -89.5450,
            categoryId = 1,
            status = BusinessStatus.ACTIVE,
        ),
        Business(
            id = 2,
            name = "Farmacia San Miguel",
            description = "Farmacia de barrio con medicamentos y productos basicos.",
            address = "8a Calle 3-18, Barrio San Miguel",
            phone = "5550-0102",
            latitude = 14.7967,
            longitude = -89.5460,
            categoryId = 2,
            status = BusinessStatus.ACTIVE,
        ),
        Business(
            id = 3,
            name = "Restaurante Sabor del Barrio",
            description = "Comida casera con servicio en mesa y pedidos para llevar.",
            address = "5a Calle 9-07, Mercado Local",
            phone = "5550-0103",
            latitude = 14.7991,
            longitude = -89.5442,
            categoryId = 3,
            status = BusinessStatus.ACTIVE,
        ),
        Business(
            id = 4,
            name = "Taller Ruta Segura",
            description = "Mantenimiento preventivo y reparacion de vehiculos livianos.",
            address = "Salida al Periferico, Lote 14",
            phone = "5550-0104",
            latitude = 14.8010,
            longitude = -89.5475,
            categoryId = 4,
            status = BusinessStatus.ACTIVE,
        ),
        Business(
            id = 5,
            name = "Clinica Familiar Centro",
            description = "Atencion medica general con consulta programada y por demanda.",
            address = "3a Avenida 7-50, Zona Centro",
            phone = "5550-0105",
            latitude = 14.7989,
            longitude = -89.5398,
            categoryId = 5,
            status = BusinessStatus.ACTIVE,
        ),
        Business(
            id = 6,
            name = "Tienda La Esquina",
            description = "Tienda de conveniencia con abarrotes y productos de uso diario.",
            address = "10a Calle 1-12, Colonia El Parque",
            phone = "5550-0106",
            latitude = 14.8000,
            longitude = -89.5453,
            categoryId = 6,
            status = BusinessStatus.ACTIVE,
        ),
    )

    val ratings = listOf(
        Rating(
            id = 1,
            userId = 2,
            businessId = 2,
            ratedOn = "2026-04-30",
            reportedPrice = 35.00,
            priceReportedOn = "2026-04-30",
            serviceScore = 5,
            attentionScore = 4,
            satisfactionScore = 5,
            waitTime = WaitTimeOption.UP_TO_15_MINUTES,
            wouldRecommend = true,
            usageFrequency = UsageFrequencyOption.OCCASIONAL,
            availability = AvailabilityOption.AVAILABLE,
            serviceMode = ServiceModeOption.IN_PERSON,
        ),
        Rating(
            id = 2,
            userId = 1,
            businessId = 2,
            ratedOn = "2026-05-01",
            reportedPrice = 42.50,
            priceReportedOn = "2026-05-01",
            serviceScore = 4,
            attentionScore = 4,
            satisfactionScore = 4,
            waitTime = WaitTimeOption.FROM_15_TO_30_MINUTES,
            wouldRecommend = true,
            usageFrequency = UsageFrequencyOption.FREQUENT,
            availability = AvailabilityOption.AVAILABLE,
            serviceMode = ServiceModeOption.IN_PERSON,
        ),
        Rating(
            id = 3,
            userId = 2,
            businessId = 3,
            ratedOn = "2026-05-02",
            reportedPrice = 68.00,
            priceReportedOn = "2026-05-02",
            serviceScore = 4,
            attentionScore = 5,
            satisfactionScore = 4,
            waitTime = WaitTimeOption.FROM_15_TO_30_MINUTES,
            wouldRecommend = true,
            usageFrequency = UsageFrequencyOption.REGULAR,
            availability = AvailabilityOption.LIMITED,
            serviceMode = ServiceModeOption.IN_PERSON,
        ),
        Rating(
            id = 4,
            userId = 1,
            businessId = 5,
            ratedOn = "2026-05-03",
            reportedPrice = 120.00,
            priceReportedOn = "2026-05-03",
            serviceScore = 5,
            attentionScore = 5,
            satisfactionScore = 5,
            waitTime = WaitTimeOption.UP_TO_15_MINUTES,
            wouldRecommend = true,
            usageFrequency = UsageFrequencyOption.OCCASIONAL,
            availability = AvailabilityOption.AVAILABLE,
            serviceMode = ServiceModeOption.IN_PERSON,
        ),
    )

    val ratingQualities = listOf(
        RatingQuality(id = 1, ratingId = 1, qualityId = 1),
        RatingQuality(id = 2, ratingId = 1, qualityId = 2),
        RatingQuality(id = 3, ratingId = 1, qualityId = 7),
        RatingQuality(id = 4, ratingId = 2, qualityId = 3),
        RatingQuality(id = 5, ratingId = 2, qualityId = 8),
        RatingQuality(id = 6, ratingId = 3, qualityId = 2),
        RatingQuality(id = 7, ratingId = 3, qualityId = 4),
        RatingQuality(id = 8, ratingId = 4, qualityId = 5),
        RatingQuality(id = 9, ratingId = 4, qualityId = 9),
    )

    val evidences = listOf(
        Evidence(
            id = 1,
            ratingId = 1,
            filePath = "evidencias/demo/factura-farmacia-san-miguel.jpg",
            fileType = EvidenceType.IMAGE,
            uploadedOn = "2026-04-30",
        ),
    )
}
