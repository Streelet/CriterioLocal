package com.example.criteriolocal.ui.business

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.criteriolocal.domain.contract.BusinessDetailDto
import com.example.criteriolocal.domain.contract.BusinessDto
import com.example.criteriolocal.domain.contract.BusinessMetricsSummaryDto
import com.example.criteriolocal.domain.contract.QualityOptionDto
import com.example.criteriolocal.domain.contract.QualitySelectionMetricDto
import com.example.criteriolocal.ui.theme.CriterioLocalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessDetailScreen(
    uiState: BusinessDetailUiState,
    onBack: () -> Unit,
    onEvaluate: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onEvaluate(uiState.businessId) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                    )
                },
                text = {
                    Text(
                        text = "Evaluar este negocio",
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
            )
        },
    ) { innerPadding ->
        val detail = uiState.detail
        val metrics = uiState.metrics

        if (detail == null || metrics == null) {
            EmptyDetailState(modifier = Modifier.padding(innerPadding))
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 96.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item { HeaderSection(business = detail.business) }
            item { ContactSection(business = detail.business) }
            item { GeneralScoreSection(metrics = metrics) }
            item { IndicatorsSection(metrics = metrics) }
            item { PriceSection(metrics = metrics) }
            if (metrics.topQualities.isNotEmpty()) {
                item { QualitiesSection(qualities = metrics.topQualities) }
            }
        }
    }
}

@Composable
private fun HeaderSection(business: BusinessDto) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        InitialsAvatar(name = business.name)
        Text(
            text = business.name,
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = business.categoryName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun InitialsAvatar(name: String) {
    val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Box(
        modifier = Modifier
            .size(96.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun ContactSection(business: BusinessDto) {
    SectionCard {
        InfoRow(
            icon = Icons.Outlined.Place,
            label = "Direccion",
            value = business.address,
        )
        Divider()
        InfoRow(
            icon = Icons.Outlined.Phone,
            label = "Telefono",
            value = business.phone ?: "Sin telefono registrado",
        )
    }
}

@Composable
private fun GeneralScoreSection(metrics: BusinessMetricsSummaryDto) {
    SectionLabel(text = "Calificacion general")
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = metrics.generalScore?.let { String.format("%.1f", it) } ?: "—",
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = MaterialTheme.typography.displayLarge.fontSize * 1.5f),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp),
                )
            }
            Text(
                text = "${metrics.totalRatings} valoraciones registradas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun IndicatorsSection(metrics: BusinessMetricsSummaryDto) {
    SectionLabel(text = "Indicadores")
    SectionCard {
        ProgressRow(
            label = "Recomendacion",
            valueText = metrics.recommendationPercentage?.let { "${it.toInt()}%" } ?: "—",
            progress = (metrics.recommendationPercentage ?: 0.0).toFloat() / 100f,
        )
        Divider()
        ProgressRow(
            label = "Atencion",
            valueText = metrics.averageAttentionScore?.let { String.format("%.1f", it) } ?: "—",
            progress = ((metrics.averageAttentionScore ?: 0.0) / 5.0).toFloat(),
        )
    }
}

@Composable
private fun ProgressRow(
    label: String,
    valueText: String,
    progress: Float,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        )
    }
}

@Composable
private fun PriceSection(metrics: BusinessMetricsSummaryDto) {
    SectionLabel(text = "Rango de precios")
    SectionCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PricePill(label = "Minimo", value = metrics.minReportedPrice)
            PricePill(label = "Promedio", value = metrics.averageReportedPrice)
            PricePill(label = "Maximo", value = metrics.maxReportedPrice)
        }
    }
}

@Composable
private fun PricePill(
    label: String,
    value: Double?,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value?.let { "Q${String.format("%.0f", it)}" } ?: "—",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun QualitiesSection(qualities: List<QualitySelectionMetricDto>) {
    SectionLabel(text = "Cualidades mas destacadas")
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(20.dp),
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(qualities, key = { it.quality.id }) { metric ->
                QualityChip(
                    label = metric.quality.name,
                    count = metric.selectionCount,
                )
            }
        }
    }
}

@Composable
private fun QualityChip(
    label: String,
    count: Int,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = CircleShape,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(20.dp),
    ) {
        Column { content() }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp),
    )
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant),
    )
}

@Composable
private fun EmptyDetailState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Sin informacion disponible",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BusinessDetailScreenPreview() {
    val business = BusinessDto(
        id = 101L,
        googlePlaceId = null,
        name = "Cafe La Antigua",
        description = "Cafeteria de especialidad.",
        address = "Calle del Arco 5-12, Antigua Guatemala",
        phone = "+502 5555-1010",
        latitude = null,
        longitude = null,
        categoryId = 1L,
        categoryName = "Restaurantes",
        status = "ACTIVE",
    )
    val metrics = BusinessMetricsSummaryDto(
        business = business,
        totalRatings = 124,
        averageServiceScore = 4.7,
        averageAttentionScore = 4.6,
        averageSatisfactionScore = 4.7,
        generalScore = 4.7,
        recommendationPercentage = 92.0,
        minReportedPrice = 25.0,
        maxReportedPrice = 80.0,
        averageReportedPrice = 48.0,
        topQualities = listOf(
            QualitySelectionMetricDto(
                quality = QualityOptionDto(1L, "Atencion amable", "", 1L),
                selectionCount = 84,
            ),
            QualitySelectionMetricDto(
                quality = QualityOptionDto(2L, "Ambiente acogedor", "", 1L),
                selectionCount = 71,
            ),
            QualitySelectionMetricDto(
                quality = QualityOptionDto(3L, "Producto fresco", "", 1L),
                selectionCount = 63,
            ),
        ),
    )
    CriterioLocalTheme {
        BusinessDetailScreen(
            uiState = BusinessDetailUiState(
                businessId = 101L,
                detail = BusinessDetailDto(business = business, ratings = emptyList()),
                metrics = metrics,
            ),
            onBack = {},
            onEvaluate = {},
        )
    }
}
