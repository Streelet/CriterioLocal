package com.example.criteriolocal.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.criteriolocal.domain.model.Business
import com.example.criteriolocal.domain.model.BusinessStatus
import com.example.criteriolocal.domain.model.BusinessWithCategory
import com.example.criteriolocal.domain.model.CatalogStatus
import com.example.criteriolocal.domain.model.Category
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
                title = { Text("CriterioLocal · Fase 1") },
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
                            "Persistencia local configurada con Room.",
                            "Entidades base: usuario, categoría, negocio, valoración, cualidad, vínculo y evidencia.",
                            "Catálogos cerrados preparados para formularios estructurados.",
                        ),
                    )
                }

                uiState.errorMessage?.let { message ->
                    item {
                        SummaryCard(
                            title = "Estado de inicialización",
                            lines = listOf(message),
                        )
                    }
                }

                item {
                    SummaryCard(
                        title = "Aviso ético",
                        lines = listOf(uiState.ethicalNotice),
                    )
                }

                item {
                    SummaryCard(
                        title = "Catálogos de valoración",
                        lines = listOf(
                            "Escala numérica: ${uiState.ratingScale.joinToString()}",
                            "Tiempo de espera: ${uiState.waitTimeOptions.joinToString { it.label }}",
                            "Frecuencia de uso: ${uiState.usageFrequencyOptions.joinToString { it.label }}",
                            "Disponibilidad: ${uiState.availabilityOptions.joinToString { it.label }}",
                            "Tipo de atención: ${uiState.serviceModeOptions.joinToString { it.label }}",
                        ),
                    )
                }

                item {
                    SummaryCard(
                        title = "Categorías iniciales",
                        lines = uiState.categories.map { "${it.id}. ${it.name}: ${it.description}" },
                    )
                }

                item {
                    SummaryCard(
                        title = "Negocios demo",
                        lines = uiState.businesses.map {
                            "${it.business.name} · ${it.category.name} · ${it.business.address}"
                        },
                    )
                }

                item {
                    SummaryCard(
                        title = "Cualidades oficiales",
                        lines = uiState.qualities.map { quality ->
                            val applicability = quality.applicableCategoryId?.let { " · categoría $it" } ?: ""
                            "${quality.name}$applicability"
                        },
                    )
                }

                item {
                    SummaryCard(
                        title = "Usuarios semilla",
                        lines = uiState.users.map { "${it.name} · ${it.email}" },
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    CriterioLocalTheme {
        HomeScreen(
            uiState = HomeUiState(
                isLoading = false,
                categories = listOf(
                    Category(1, "Laboratorio clínico", "Pruebas diagnósticas."),
                    Category(2, "Farmacia", "Venta de medicamentos."),
                ),
                businesses = listOf(
                    BusinessWithCategory(
                        business = Business(
                            id = 1,
                            name = "Laboratorio Vida",
                            description = "Atención diagnóstica.",
                            address = "4a Avenida 12-45",
                            phone = "5550-0101",
                            categoryId = 1,
                            status = BusinessStatus.ACTIVE,
                        ),
                        category = Category(1, "Laboratorio clínico", "Pruebas diagnósticas."),
                    ),
                ),
                qualities = listOf(
                    Quality(1, "Atención rápida", "Atención en poco tiempo.", null, CatalogStatus.ACTIVE),
                    Quality(2, "Trato amable", "Atención cordial.", null, CatalogStatus.ACTIVE),
                ),
            ),
        )
    }
}
