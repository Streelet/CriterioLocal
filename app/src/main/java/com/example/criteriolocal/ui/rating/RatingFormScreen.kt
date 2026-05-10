package com.example.criteriolocal.ui.rating

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.criteriolocal.ui.theme.CriterioLocalTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingFormScreen(
    uiState: RatingFormUiState,
    events: Flow<RatingFormEvent>,
    onServiceScoreChange: (Int) -> Unit,
    onAttentionScoreChange: (Int) -> Unit,
    onSatisfactionScoreChange: (Int) -> Unit,
    onPriceChange: (String) -> Unit,
    onPriceDateChange: (Long) -> Unit,
    onWaitTimeSelected: (String) -> Unit,
    onUsageFrequencySelected: (String) -> Unit,
    onAvailabilitySelected: (String) -> Unit,
    onRecommendationChange: (Boolean) -> Unit,
    onQualityToggled: (Long) -> Unit,
    onAttachEvidence: () -> Unit,
    onRemoveEvidence: () -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    onSubmitted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                RatingFormEvent.SubmittedSuccessfully -> {
                    Toast.makeText(
                        context,
                        "Valoracion enviada con exito",
                        Toast.LENGTH_SHORT,
                    ).show()
                    onSubmitted()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Evaluar negocio",
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
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 32.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item { IntroSection() }

            item {
                ScoresSection(
                    serviceScore = uiState.serviceScore,
                    attentionScore = uiState.attentionScore,
                    satisfactionScore = uiState.satisfactionScore,
                    onServiceScoreChange = onServiceScoreChange,
                    onAttentionScoreChange = onAttentionScoreChange,
                    onSatisfactionScoreChange = onSatisfactionScoreChange,
                )
            }

            item {
                PriceSection(
                    price = uiState.reportedPrice,
                    onPriceChange = onPriceChange,
                    dateMillis = uiState.priceDateMillis,
                    onOpenDatePicker = { showDatePicker = true },
                )
            }

            item {
                ChoiceSection(
                    title = "Tiempo de espera",
                    options = uiState.waitTimeOptions,
                    selectedCode = uiState.selectedWaitTime,
                    onSelected = onWaitTimeSelected,
                )
            }

            item {
                ChoiceSection(
                    title = "Frecuencia de uso",
                    options = uiState.usageFrequencyOptions,
                    selectedCode = uiState.selectedUsageFrequency,
                    onSelected = onUsageFrequencySelected,
                )
            }

            item {
                ChoiceSection(
                    title = "Disponibilidad",
                    options = uiState.availabilityOptions,
                    selectedCode = uiState.selectedAvailability,
                    onSelected = onAvailabilitySelected,
                )
            }

            item {
                RecommendationSection(
                    wouldRecommend = uiState.wouldRecommend,
                    onChange = onRecommendationChange,
                )
            }

            item {
                QualitiesSection(
                    options = uiState.qualityOptions,
                    selectedIds = uiState.selectedQualityIds,
                    onToggle = onQualityToggled,
                )
            }

            item {
                EvidenceSection(
                    evidence = uiState.evidence,
                    onAttach = onAttachEvidence,
                    onRemove = onRemoveEvidence,
                )
            }

            item { EthicalNoticeCard(notice = uiState.ethicalNotice) }

            uiState.errorMessage?.let { message ->
                item {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
            }

            item {
                SubmitButton(
                    enabled = uiState.canSubmit,
                    isSubmitting = uiState.isSubmitting,
                    onClick = onSubmit,
                )
            }
        }
    }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.priceDateMillis,
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let(onPriceDateChange)
                    showDatePicker = false
                }) {
                    Text(text = "Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(text = "Cancelar")
                }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun IntroSection() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Cuentanos tu experiencia",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "Tus respuestas se registran como datos objetivos. No incluyas comentarios libres.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ScoresSection(
    serviceScore: Int,
    attentionScore: Int,
    satisfactionScore: Int,
    onServiceScoreChange: (Int) -> Unit,
    onAttentionScoreChange: (Int) -> Unit,
    onSatisfactionScoreChange: (Int) -> Unit,
) {
    SectionLabel(text = "Calificaciones")
    SectionCard {
        StarRatingRow(
            label = "Servicio",
            score = serviceScore,
            onChange = onServiceScoreChange,
        )
        Divider()
        StarRatingRow(
            label = "Atencion",
            score = attentionScore,
            onChange = onAttentionScoreChange,
        )
        Divider()
        StarRatingRow(
            label = "Satisfaccion",
            score = satisfactionScore,
            onChange = onSatisfactionScoreChange,
        )
    }
}

@Composable
private fun StarRatingRow(
    label: String,
    score: Int,
    onChange: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = if (score > 0) "$score / 5" else "Sin calificar",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            (1..5).forEach { value ->
                val isFilled = value <= score
                IconButton(
                    onClick = { onChange(value) },
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        imageVector = if (isFilled) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = "Calificar $value",
                        tint = if (isFilled) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PriceSection(
    price: String,
    onPriceChange: (String) -> Unit,
    dateMillis: Long,
    onOpenDatePicker: () -> Unit,
) {
    SectionLabel(text = "Precio reportado")
    SectionCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            OutlinedTextField(
                value = price,
                onValueChange = onPriceChange,
                label = {
                    Text(
                        text = "Precio pagado o cotizado (Q)",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            DateTrigger(
                dateMillis = dateMillis,
                onClick = onOpenDatePicker,
            )
        }
    }
}

@Composable
private fun DateTrigger(
    dateMillis: Long,
    onClick: () -> Unit,
) {
    val formatter = remember {
        SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es"))
    }
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = "Fecha del precio",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = formatter.format(Date(dateMillis)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun ChoiceSection(
    title: String,
    options: List<CatalogChoice>,
    selectedCode: String?,
    onSelected: (String) -> Unit,
) {
    SectionLabel(text = title)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(20.dp),
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(options, key = { it.code }) { option ->
                ChoicePill(
                    label = option.label,
                    isSelected = option.code == selectedCode,
                    onClick = { onSelected(option.code) },
                )
            }
        }
    }
}

@Composable
private fun ChoicePill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val container = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }
    val content = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    Surface(
        color = container,
        shape = CircleShape,
        modifier = Modifier
            .heightIn(min = 36.dp)
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = content,
            )
        }
    }
}

@Composable
private fun RecommendationSection(
    wouldRecommend: Boolean?,
    onChange: (Boolean) -> Unit,
) {
    SectionLabel(text = "Recomendacion")
    SectionCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Recomendarias este lugar?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ChoicePill(
                    label = "Si",
                    isSelected = wouldRecommend == true,
                    onClick = { onChange(true) },
                )
                ChoicePill(
                    label = "No",
                    isSelected = wouldRecommend == false,
                    onClick = { onChange(false) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QualitiesSection(
    options: List<QualityChoice>,
    selectedIds: Set<Long>,
    onToggle: (Long) -> Unit,
) {
    SectionLabel(text = "Cualidades destacadas")
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Selecciona las que apliquen",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(options, key = { it.id }) { option ->
                    val isSelected = option.id in selectedIds
                    FilterChip(
                        selected = isSelected,
                        onClick = { onToggle(option.id) },
                        label = {
                            Text(
                                text = option.label,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        },
                        shape = CircleShape,
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            labelColor = MaterialTheme.colorScheme.onSurface,
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                        border = null,
                    )
                }
            }
        }
    }
}

@Composable
private fun EvidenceSection(
    evidence: EvidenceAttachment?,
    onAttach: () -> Unit,
    onRemove: () -> Unit,
) {
    SectionLabel(text = "Evidencia")
    if (evidence == null) {
        AttachEvidenceButton(onClick = onAttach)
    } else {
        EvidenceAttachedCard(evidence = evidence, onRemove = onRemove)
    }
}

@Composable
private fun AttachEvidenceButton(onClick: () -> Unit) {
    val outlineColor = MaterialTheme.colorScheme.outline
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(onClick = onClick)
            .drawBehind {
                val radius = CornerRadius(18.dp.toPx())
                drawRoundRect(
                    color = outlineColor,
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f),
                    ),
                    cornerRadius = radius,
                )
            }
            .padding(horizontal = 20.dp, vertical = 22.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.PhotoCamera,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Adjuntar evidencia (Opcional)",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Max 5MB - foto de factura o cotizacion",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun EvidenceAttachedCard(
    evidence: EvidenceAttachment,
    onRemove: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(18.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = RoundedCornerShape(12.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.InsertDriveFile,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = evidence.fileName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = formatFileSize(evidence.sizeBytes),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Remover evidencia",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    val mb = bytes / 1024.0 / 1024.0
    return if (mb >= 1.0) {
        String.format("%.1f MB", mb)
    } else {
        val kb = bytes / 1024.0
        String.format("%.0f KB", kb)
    }
}

@Composable
private fun EthicalNoticeCard(notice: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Aviso de uso etico",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = notice,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SubmitButton(
    enabled: Boolean,
    isSubmitting: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
    ) {
        if (isSubmitting) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Enviando...",
                style = MaterialTheme.typography.labelLarge,
            )
        } else {
            Text(
                text = "Enviar valoracion",
                style = MaterialTheme.typography.labelLarge,
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
        Column(modifier = Modifier.fillMaxWidth()) { content() }
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
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant),
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RatingFormScreenPreview() {
    val sampleState = RatingFormUiState(
        businessId = 101L,
        serviceScore = 4,
        attentionScore = 5,
        satisfactionScore = 4,
        reportedPrice = "75",
        waitTimeOptions = listOf(
            CatalogChoice("UNDER_5", "Menos de 5 min"),
            CatalogChoice("BETWEEN_5_15", "5 a 15 min"),
            CatalogChoice("BETWEEN_15_30", "15 a 30 min"),
        ),
        selectedWaitTime = "BETWEEN_5_15",
        usageFrequencyOptions = listOf(
            CatalogChoice("FIRST_TIME", "Primera vez"),
            CatalogChoice("MONTHLY", "Mensual"),
        ),
        selectedUsageFrequency = "MONTHLY",
        availabilityOptions = listOf(
            CatalogChoice("IMMEDIATE", "Inmediata"),
            CatalogChoice("APPOINTMENT", "Con cita"),
        ),
        selectedAvailability = "IMMEDIATE",
        wouldRecommend = true,
        qualityOptions = listOf(
            QualityChoice(1L, "Trato amable"),
            QualityChoice(2L, "Precio accesible"),
            QualityChoice(3L, "Puntualidad"),
            QualityChoice(4L, "Limpieza"),
        ),
        selectedQualityIds = setOf(1L, 3L),
    )
    CriterioLocalTheme {
        RatingFormScreen(
            uiState = sampleState,
            events = emptyFlow(),
            onServiceScoreChange = {},
            onAttentionScoreChange = {},
            onSatisfactionScoreChange = {},
            onPriceChange = {},
            onPriceDateChange = {},
            onWaitTimeSelected = {},
            onUsageFrequencySelected = {},
            onAvailabilitySelected = {},
            onRecommendationChange = {},
            onQualityToggled = {},
            onAttachEvidence = {},
            onRemoveEvidence = {},
            onSubmit = {},
            onBack = {},
            onSubmitted = {},
        )
    }
}
