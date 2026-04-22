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
        Category(1, "Laboratorio clinico", "Servicios de analisis y pruebas diagnosticas."),
        Category(2, "Farmacia", "Venta de medicamentos y productos de cuidado personal."),
        Category(3, "Restaurante", "Negocios de alimentos preparados y atencion en mesa o para llevar."),
        Category(4, "Taller mecanico", "Servicios de mantenimiento y reparacion vehicular."),
        Category(5, "Clinica medica", "Consultas, atencion medica general y especialidades."),
        Category(6, "Tienda local", "Comercios de conveniencia y productos del dia a dia."),
    )

    val qualities = listOf(
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
}
