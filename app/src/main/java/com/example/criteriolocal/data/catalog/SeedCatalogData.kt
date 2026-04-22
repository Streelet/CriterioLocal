package com.example.criteriolocal.data.catalog

import com.example.criteriolocal.domain.model.Business
import com.example.criteriolocal.domain.model.BusinessStatus
import com.example.criteriolocal.domain.model.CatalogStatus
import com.example.criteriolocal.domain.model.Category
import com.example.criteriolocal.domain.model.Quality
import com.example.criteriolocal.domain.model.User
import com.example.criteriolocal.domain.model.UserStatus

object SeedCatalogData {
    val categories = listOf(
        Category(1, "Laboratorio clínico", "Servicios de análisis y pruebas diagnósticas."),
        Category(2, "Farmacia", "Venta de medicamentos y productos de cuidado personal."),
        Category(3, "Restaurante", "Negocios de alimentos preparados y atención en mesa o para llevar."),
        Category(4, "Taller mecánico", "Servicios de mantenimiento y reparación vehicular."),
        Category(5, "Clínica médica", "Consultas, atención médica general y especialidades."),
        Category(6, "Tienda local", "Comercios de conveniencia y productos del día a día."),
    )

    val qualities = listOf(
        Quality(1, "Atención rápida", "El servicio se completó en poco tiempo.", null, CatalogStatus.ACTIVE),
        Quality(2, "Trato amable", "La atención fue cordial y respetuosa.", null, CatalogStatus.ACTIVE),
        Quality(3, "Precio accesible", "El precio reportado fue percibido como competitivo.", null, CatalogStatus.ACTIVE),
        Quality(4, "Higiene adecuada", "Las instalaciones o procesos mostraron limpieza adecuada.", null, CatalogStatus.ACTIVE),
        Quality(5, "Buena explicación del servicio", "Se explicó claramente el producto o procedimiento.", null, CatalogStatus.ACTIVE),
        Quality(6, "Puntualidad", "Se cumplió el tiempo prometido para la atención o entrega.", null, CatalogStatus.ACTIVE),
        Quality(7, "Variedad de productos", "Se observó surtido suficiente en el negocio.", 2, CatalogStatus.ACTIVE),
        Quality(8, "Servicio confiable", "La experiencia transmitió confianza y seguridad.", null, CatalogStatus.ACTIVE),
        Quality(9, "Diagnóstico claro", "La explicación clínica fue entendible y precisa.", 5, CatalogStatus.ACTIVE),
        Quality(10, "Repuestos disponibles", "El negocio tuvo piezas o insumos necesarios al momento.", 4, CatalogStatus.ACTIVE),
    )

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
            description = "Laboratorio de análisis clínicos con atención ambulatoria.",
            address = "4a Avenida 12-45, Zona Centro",
            phone = "5550-0101",
            categoryId = 1,
            status = BusinessStatus.ACTIVE,
        ),
        Business(
            id = 2,
            name = "Farmacia San Miguel",
            description = "Farmacia de barrio con medicamentos y productos básicos.",
            address = "8a Calle 3-18, Barrio San Miguel",
            phone = "5550-0102",
            categoryId = 2,
            status = BusinessStatus.ACTIVE,
        ),
        Business(
            id = 3,
            name = "Restaurante Sabor del Barrio",
            description = "Comida casera con servicio en mesa y pedidos para llevar.",
            address = "5a Calle 9-07, Mercado Local",
            phone = "5550-0103",
            categoryId = 3,
            status = BusinessStatus.ACTIVE,
        ),
        Business(
            id = 4,
            name = "Taller Ruta Segura",
            description = "Mantenimiento preventivo y reparación de vehículos livianos.",
            address = "Salida al Periférico, Lote 14",
            phone = "5550-0104",
            categoryId = 4,
            status = BusinessStatus.ACTIVE,
        ),
        Business(
            id = 5,
            name = "Clínica Familiar Centro",
            description = "Atención médica general con consulta programada y por demanda.",
            address = "3a Avenida 7-50, Zona Centro",
            phone = "5550-0105",
            categoryId = 5,
            status = BusinessStatus.ACTIVE,
        ),
        Business(
            id = 6,
            name = "Tienda La Esquina",
            description = "Tienda de conveniencia con abarrotes y productos de uso diario.",
            address = "10a Calle 1-12, Colonia El Parque",
            phone = "5550-0106",
            categoryId = 6,
            status = BusinessStatus.ACTIVE,
        ),
    )
}
