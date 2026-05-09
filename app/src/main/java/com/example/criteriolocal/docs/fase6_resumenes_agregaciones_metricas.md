# Fase 6. Resumenes, agregaciones y metricas

## 1. Resumen de la fase

Esta fase implementa calculos agregados para apoyar comparaciones objetivas entre negocios locales usando valoraciones estructuradas.

No se agregan pantallas nuevas ni reglas de registro. La salida queda lista para alimentar paneles de resumen, detalle de negocio y pantallas comparativas.

## 2. Logica de calculo implementada

### Resumen por negocio

`BusinessMetricsManager.observeBusinessSummary(businessId)` calcula:

- total de valoraciones,
- promedio de servicio,
- promedio de atencion,
- promedio de satisfaccion,
- puntaje general,
- porcentaje de recomendacion,
- precio minimo reportado,
- precio maximo reportado,
- precio promedio reportado,
- cualidades mas seleccionadas.

### Formula de puntaje general

La formula usada es:

`puntaje_general = (promedio_servicio + promedio_atencion + promedio_satisfaccion) / 3`

Si un negocio no tiene valoraciones, sus metricas numericas quedan en `null` y `totalRatings = 0`.

### Porcentaje de recomendacion

La formula usada es:

`porcentaje_recomendacion = recomendaciones_positivas / total_valoraciones * 100`

### Rango y promedio de precio

Se calculan desde `reportedPrice`:

- minimo,
- maximo,
- promedio.

### Cualidades mas seleccionadas

Se cuentan las cualidades asociadas a valoraciones del negocio y se ordenan por:

1. mayor cantidad de selecciones,
2. nombre de cualidad en orden alfabetico.

### Ranking por categoria

`BusinessMetricsManager.observeCategoryRanking(categoryId)` genera ranking solo para negocios con al menos una valoracion.

Ordenamiento:

1. mayor `puntaje_general`,
2. mayor `porcentaje_recomendacion`,
3. mayor cantidad de valoraciones,
4. nombre del negocio.

## 3. Archivos creados o modificados

### Creados

- `domain/metrics/BusinessMetricsModels.kt`
- `domain/metrics/BusinessMetricsManager.kt`
- `src/test/.../domain/metrics/BusinessMetricsManagerTest.kt`
- `docs/fase6_resumenes_agregaciones_metricas.md`

### Modificados

- `core/di/AppContainer.kt`

## 4. Pruebas agregadas

`BusinessMetricsManagerTest` cubre:

- promedios de servicio, atencion y satisfaccion,
- puntaje general,
- porcentaje de recomendacion,
- precio minimo, maximo y promedio,
- cualidades mas seleccionadas,
- negocio inexistente,
- negocios sin valoraciones,
- ranking por categoria con desempate por recomendacion.

## 5. Requerimientos cubiertos

### RF

- RF-22: promedios de calificacion.
- RF-23: rangos de precios reportados.
- RF-24: base objetiva para comparacion.
- RF-25: promedio de atencion.
- RF-26: promedio de precio reportado.
- RF-27: porcentaje de recomendacion.
- RF-28: cualidades mas seleccionadas.
- RF-29: negocios mejor valorados por categoria.

### RLD

- RLD-06: promedios de atencion, servicio y satisfaccion.
- RLD-07: porcentaje de recomendacion.
- RLD-08: rangos de precio minimo, maximo y promedio.
- RLD-09: cualidades mas seleccionadas.
- RLD-10: ranking de negocios por categoria.

## 6. Estructura lista para paneles

Los modelos listos para UI son:

- `BusinessMetricsSummary`
- `QualitySelectionMetric`
- `CategoryRanking`
- `CategoryRankingItem`

Estos modelos pueden alimentar:

- detalle de negocio,
- panel de resumen,
- listado comparativo,
- ranking por categoria.
