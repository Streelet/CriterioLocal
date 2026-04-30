# Fase 5. Consultas, filtros e historial

## 1. Resumen de la fase

Esta fase implementa la logica de consulta para encontrar negocios, consultar detalle basico y revisar historial personal de valoraciones.

El alcance se mantiene cerrado:

- No se implementan rankings avanzados.
- No se calculan paneles de metricas agregadas.
- No se generan comparativas complejas.
- Los filtros se limitan a datos ya disponibles: nombre, categoria, precio reportado y calificacion promedio basica.

## 2. Consultas implementadas

### Busqueda por nombre

`BusinessQueryManager.searchBusinessesByName(query)` filtra negocios por coincidencia parcial del nombre sin distinguir mayusculas/minusculas.

### Busqueda por categoria

`BusinessQueryManager.searchBusinessesByCategory(categoryId)` devuelve negocios asociados a una categoria local.

### Listado filtrado

`BusinessQueryManager.searchBusinesses(filters)` permite combinar:

- `nameQuery`,
- `categoryId`,
- `minAverageScore`,
- `minReportedPrice`,
- `maxReportedPrice`.

La calificacion usada en este filtro es basica:

`averageScore = promedio de servicio, atencion y satisfaccion de las valoraciones existentes`

Esto no es ranking. Solo permite filtrar por umbral minimo.

### Detalle de negocio

`BusinessQueryManager.observeBusinessDetail(businessId)` devuelve:

- negocio con categoria,
- valoraciones asociadas al negocio.

### Historial de usuario

`BusinessQueryManager.observeUserRatingHistory(userId)` devuelve las valoraciones asociadas a un usuario.

## 3. Archivos creados o modificados

### Creados

- `domain/query/BusinessQueryModels.kt`
- `domain/query/BusinessQueryManager.kt`
- `src/test/.../domain/query/BusinessQueryManagerTest.kt`
- `docs/fase5_consultas_filtros_historial.md`

### Modificados

- `domain/repository/RepositoryInterfaces.kt`
- `data/local/dao/Daos.kt`
- `data/repository/DefaultRepositories.kt`
- `core/di/AppContainer.kt`
- `src/test/.../domain/management/RatingRegistrationManagerTest.kt`

## 4. Reglas aplicadas

- Las consultas trabajan sobre negocios existentes, principalmente importados/vinculados desde Google Places.
- El historial solo usa valoraciones estructuradas ya registradas.
- Los filtros por precio y calificacion no modifican datos.
- Los filtros no crean rankings ni resumenes globales.
- Si un negocio no existe, el detalle devuelve `null`.

## 5. Pruebas agregadas

`BusinessQueryManagerTest` cubre:

- busqueda por nombre,
- busqueda por categoria,
- filtros basicos por calificacion y precio,
- detalle de negocio con valoraciones,
- detalle nulo para negocio inexistente,
- historial de valoraciones por usuario.

## 6. Requerimientos cubiertos

### RF

- RF-20: busqueda de negocios por nombre.
- RF-21: busqueda de negocios por categoria.
- RF-22 parcial: consulta de calificacion promedio basica para filtrar.
- RF-23 parcial: consulta de precio minimo y maximo basico para filtrar.
- RF-24 parcial: estructura inicial para filtrar opciones por criterios objetivos.

### RLD

- RLD-04: consultas por nombre, categoria, precio y calificacion.
- RLD-05: filtros basicos para comparacion objetiva futura.

## 7. Pendientes para fase de metricas

- Promedio formal de atencion por negocio.
- Promedio formal de servicio por negocio.
- Promedio formal de satisfaccion por negocio.
- Porcentaje de recomendacion.
- Precio minimo, maximo y promedio reportado.
- Cualidades mas seleccionadas.
- Rankings por categoria.
- Comparativas avanzadas entre negocios.
