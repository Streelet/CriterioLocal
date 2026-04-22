package com.example.criteriolocal.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.criteriolocal.domain.model.Business
import com.example.criteriolocal.domain.model.BusinessStatus
import com.example.criteriolocal.domain.model.BusinessWithCategory
import com.example.criteriolocal.domain.model.CatalogStatus
import com.example.criteriolocal.domain.model.Category
import com.example.criteriolocal.domain.model.NearbyPlace
import com.example.criteriolocal.domain.model.Quality
import com.example.criteriolocal.ui.theme.CriterioLocalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("CriterioLocal - Fase 1") },
            )
        },
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    SummaryCard(
                        title = "Base estructural lista",
                        lines = listOf(
                            "Capas: core, data, domain y ui.",
                            "Persistencia hibrida preparada: Room local + Google Places remoto.",
                            "Entidades base: usuario, categoria, negocio, valoracion, cualidad, vinculo y evidencia.",
                            "Catalogos cerrados y cliente HTTP listos para fases posteriores.",
                            "API key remota inyectada desde variable de entorno en compilacion.",
                        ),
                    )
                }

                uiState.remotePlacesStatus?.let { status ->
                    item {
                        SummaryCard(
                            title = "Estado de la API",
                            lines = listOf("Nearby Search: $status"),
                        )
                    }
                }

                uiState.remotePlacesError?.let { message ->
                    item {
                        SummaryCard(
                            title = "Estado de la API",
                            lines = listOf(message),
                        )
                    }
                }

                uiState.errorMessage?.let { message ->
                    item {
                        SummaryCard(
                            title = "Estado de inicializacion",
                            lines = listOf(message),
                        )
                    }
                }

                item {
                    SummaryCard(
                        title = "Aviso etico",
                        lines = listOf(uiState.ethicalNotice),
                    )
                }

                item {
                    SummaryCard(
                        title = "Catalogos de valoracion",
                        lines = listOf(
                            "Escala numerica: ${uiState.ratingScale.joinToString()}",
                            "Tiempo de espera: ${uiState.waitTimeOptions.joinToString { it.label }}",
                            "Frecuencia de uso: ${uiState.usageFrequencyOptions.joinToString { it.label }}",
                            "Disponibilidad: ${uiState.availabilityOptions.joinToString { it.label }}",
                            "Tipo de atencion: ${uiState.serviceModeOptions.joinToString { it.label }}",
                        ),
                    )
                }

                item {
                    SummaryCard(
                        title = "Categorias iniciales",
                        lines = uiState.categories.map { "${it.id}. ${it.name}: ${it.description}" },
                    )
                }

                item {
                    SummaryCard(
                        title = if (uiState.remotePlaces.isNotEmpty()) {
                            "Negocios demo consumidos desde la API"
                        } else {
                            "Negocios demo locales"
                        },
                        lines = if (uiState.remotePlaces.isNotEmpty()) {
                            listOf(
                                "Se muestran los negocios remotos con imagen cuando Google Places devuelve fotos.",
                            )
                        } else {
                            uiState.businesses.map {
                                buildString {
                                    append(it.business.name)
                                    append(" - ")
                                    append(it.category.name)
                                    append(" - ")
                                    append(it.business.address)
                                    val coordinates = listOfNotNull(it.business.latitude, it.business.longitude)
                                    if (coordinates.size == 2) {
                                        append(" - ")
                                        append("${coordinates[0]}, ${coordinates[1]}")
                                    }
                                }
                            }
                        },
                    )
                }

                if (uiState.remotePlaces.isNotEmpty()) {
                    items(uiState.remotePlaces, key = { it.googlePlaceId }) { place ->
                        RemotePlaceCard(place = place)
                    }
                }

                item {
                    SummaryCard(
                        title = "Negocios locales base",
                        lines = uiState.businesses.map {
                            "${it.business.name} - ${it.category.name} - ${it.business.address}"
                        },
                    )
                }

                item {
                    SummaryCard(
                        title = "Cualidades oficiales",
                        lines = uiState.qualities.map { quality ->
                            val applicability = quality.applicableCategoryId?.let { " - categoria $it" } ?: ""
                            "${quality.name}$applicability"
                        },
                    )
                }

                item {
                    SummaryCard(
                        title = "Usuarios semilla",
                        lines = uiState.users.map { "${it.name} - ${it.email}" },
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    lines: List<String>,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            lines.forEach { line ->
                Text(
                    text = line,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun RemotePlaceCard(
    place: NearbyPlace,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val imageUrl = place.photoUrl ?: place.iconUrl
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = place.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop,
                )
            }

            Text(
                text = place.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            place.address?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Text(
                text = "Coordenadas: ${place.latitude}, ${place.longitude}",
                style = MaterialTheme.typography.bodySmall,
            )

            val details = buildList {
                place.rating?.let { add("Rating $it") }
                place.userRatingsTotal?.let { add("Opiniones $it") }
                place.phone?.takeIf { it.isNotBlank() }?.let { add(it) }
                place.isOpenNow?.let { add(if (it) "Abierto ahora" else "Cerrado ahora") }
            }
            if (details.isNotEmpty()) {
                Text(
                    text = details.joinToString(" - "),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            place.photoUrl?.let {
                Text(
                    text = "Foto API: $it",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    CriterioLocalTheme {
        HomeScreen(
            uiState = HomeUiState(
                isLoading = false,
                categories = listOf(
                    Category(1, "Laboratorio clinico", "Pruebas diagnosticas."),
                    Category(2, "Farmacia", "Venta de medicamentos."),
                ),
                businesses = listOf(
                    BusinessWithCategory(
                        business = Business(
                            id = 1,
                            name = "Laboratorio Vida",
                            description = "Atencion diagnostica.",
                            address = "4a Avenida 12-45",
                            phone = "5550-0101",
                            latitude = 14.7924,
                            longitude = -89.5450,
                            categoryId = 1,
                            status = BusinessStatus.ACTIVE,
                        ),
                        category = Category(1, "Laboratorio clinico", "Pruebas diagnosticas."),
                    ),
                ),
                qualities = listOf(
                    Quality(1, "Atencion rapida", "Atencion en poco tiempo.", null, CatalogStatus.ACTIVE),
                    Quality(2, "Trato amable", "Atencion cordial.", null, CatalogStatus.ACTIVE),
                ),
                remotePlaces = listOf(
                    NearbyPlace(
                        googlePlaceId = "sample-place-id",
                        name = "Farmacia Demo API",
                        businessStatus = "OPERATIONAL",
                        latitude = 14.7924897,
                        longitude = -89.5450458,
                        address = "4a Calle 1-70, Chiquimula",
                        phone = "+502 3220 3463",
                        types = listOf("pharmacy", "store"),
                        rating = 4.8,
                        userRatingsTotal = 24,
                        isOpenNow = true,
                        iconUrl = "https://maps.gstatic.com/mapfiles/place_api/icons/v1/png_71/pharmacy-71.png",
                        photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&photo_reference=demo&key=demo",
                    ),
                ),
                remotePlacesStatus = "OK",
            ),
        )
    }
}
