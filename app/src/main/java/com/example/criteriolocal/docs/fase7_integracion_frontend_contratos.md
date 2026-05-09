# Fase 7. Integracion con frontend y contratos de datos

## 1. Resumen de la fase

Esta fase define una capa backend de contratos para que el frontend consuma datos sin depender directamente de entidades locales, relaciones Room ni modelos internos complejos.

No se crean pantallas, componentes Compose ni logica visual. El trabajo queda limitado a DTOs, mapeos, respuestas y un facade preparado para ViewModels.

## 2. Contratos de datos definidos

### Respuesta estandar

- `ContractResultDto<T>`
- `ContractErrorDto`

Todas las operaciones que pueden fallar devuelven errores con:

- `code`,
- `field`,
- `message`.

### Catalogos para formulario

- `RatingFormCatalogsDto`
- `CatalogOptionDto`
- `QualityOptionDto`

Incluye escala 1 a 5, tiempos de espera, frecuencia de uso, disponibilidad, tipo de atencion, cualidades activas, rango de precio permitido y aviso etico.

### Listados y busqueda

- `BusinessSearchFiltersDto`
- `BusinessListItemDto`
- `BusinessDto`

Sirven para listados, busqueda por nombre, filtros por categoria, precio y calificacion.

### Detalle e historial

- `BusinessDetailDto`
- `RatingDetailDto`
- `UserRatingHistoryDto`
- `EvidenceDto`

Sirven para pantalla de detalle de negocio e historial de valoraciones del usuario.

### Registro de valoracion

- `RatingRegistrationRequestDto`
- `EvidenceRequestDto`
- `RatingRegistrationResponseDto`

El frontend envia codigos de catalogo como texto, por ejemplo `UP_TO_15_MINUTES`, `OCCASIONAL`, `AVAILABLE`, `IN_PERSON`.

Si un codigo no existe, se devuelve error `INVALID_CATALOG_OPTION`.

### Resumenes y ranking

- `BusinessMetricsSummaryDto`
- `QualitySelectionMetricDto`
- `CategoryRankingDto`
- `CategoryRankingItemDto`

Sirven para panel de resumen y pantallas comparativas.

## 3. Archivos creados o modificados

### Creados

- `domain/contract/ContractDtos.kt`
- `domain/contract/ContractMappers.kt`
- `domain/contract/FrontendContractManager.kt`
- `src/test/.../domain/contract/ContractMappersTest.kt`
- `docs/fase7_integracion_frontend_contratos.md`

### Modificados

- `core/di/AppContainer.kt`

## 4. Facade para ViewModel

`FrontendContractManager` expone:

- `observeRatingFormCatalogs()`
- `searchBusinesses(filters)`
- `observeBusinessDetail(businessId)`
- `observeUserRatingHistory(userId)`
- `observeBusinessSummary(businessId)`
- `observeCategoryRanking(categoryId)`
- `registerStructuredRating(request)`

El frontend puede consumir esta capa desde ViewModels sin conocer los detalles internos de repositorios, Room, validadores o managers.

## 5. Estados de error para frontend

Errores principales:

- `NOT_FOUND`
- `INVALID_CATALOG_OPTION`
- codigos heredados de validacion, por ejemplo `FREE_TEXT_NOT_ALLOWED`, `QUALITY_NOT_IN_CATALOG`, `PRICE_OUT_OF_RANGE`.

Cada error incluye campo y mensaje legible.

## 6. Requerimientos cubiertos

- RLD-11: estructuras de datos reutilizables para paneles, formularios y pantallas comparativas.
- RNF-09: arquitectura ordenada por responsabilidades.
- RNF-13: separacion entre logica de negocio e interfaz de usuario.

## 7. Puntos de integracion pendientes

- Conectar `FrontendContractManager` desde ViewModels reales de Compose.
- Definir estados UI concretos por pantalla.
- Agregar eventos de UI para formularios y filtros.
- Definir navegacion entre listado, detalle, formulario e historial.
- Decidir si los DTOs se serializaran tambien para una API remota futura.
