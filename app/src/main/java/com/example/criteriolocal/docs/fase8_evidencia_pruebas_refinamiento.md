# Fase 8. Evidencia opcional, pruebas y refinamiento

## 1. Resumen de la fase

Esta fase cierra extras controlados del modulo backend. La evidencia se mantiene como una caracteristica opcional: una valoracion puede registrarse sin evidencia, pero si el usuario adjunta evidencia, el backend exige datos minimos y valida formato, tipo, tamano y fecha.

No se agregaron pantallas, rankings nuevos ni consumo adicional de API. La fase se enfoca en calidad, consistencia, pruebas y preparacion para integracion segura con frontend.

## 2. Mejoras implementadas

- Politica centralizada de evidencia en `EvidenceFilePolicy`.
- Tamano maximo de evidencia: 5 MB.
- Extensiones permitidas por tipo:
- `IMAGE`: `jpg`, `jpeg`, `png`.
- `PDF`: `pdf`.
- Validacion de ruta de archivo.
- Validacion de tipo de archivo.
- Validacion de tamano requerido, positivo y dentro del limite.
- Validacion de extension compatible con el tipo seleccionado.
- Validacion de fecha de carga en formato `YYYY-MM-DD`.
- Contrato de frontend actualizado para enviar `fileSizeBytes`.
- Catalogo de formulario actualizado para exponer la politica de evidencia al frontend.
- Datos semilla ampliados con valoraciones, cualidades asociadas y evidencia opcional.
- Bootstrap de Room actualizado para sembrar valoraciones, relaciones y evidencia.
- Pruebas unitarias nuevas para evidencia y consistencia del seed.

## 3. Archivos creados

- `domain/validation/EvidenceFilePolicy.kt`
- `test/domain/validation/EvidenceValidatorTest.kt`
- `test/data/catalog/SeedCatalogDataTest.kt`
- `docs/fase8_evidencia_pruebas_refinamiento.md`

## 4. Archivos modificados

- `domain/validation/EvidenceValidator.kt`
- `domain/validation/RatingValidationModels.kt`
- `domain/management/RatingRegistrationModels.kt`
- `domain/management/RatingRegistrationManager.kt`
- `domain/contract/ContractDtos.kt`
- `domain/contract/ContractMappers.kt`
- `data/catalog/SeedCatalogData.kt`
- `data/local/dao/Daos.kt`
- `data/repository/DefaultRepositories.kt`
- `core/di/AppContainer.kt`
- `test/domain/management/RatingRegistrationManagerTest.kt`
- `test/domain/contract/ContractMappersTest.kt`

## 5. Flujo de evidencia opcional

1. El frontend puede enviar una valoracion con `evidences = emptyList()`.
2. Si la lista esta vacia, la valoracion sigue siendo valida si cumple las reglas estructuradas.
3. Si se envia evidencia, cada item debe incluir:
- `filePath`
- `fileTypeCode`
- `fileSizeBytes`
- `uploadedOn`
4. El backend valida extension y tamano antes de persistir.
5. La entidad `Evidence` persiste la ruta, tipo y fecha. El tamano se usa como dato de control de entrada, no como dato historico obligatorio.

## 6. Codigos de error agregados

- `EVIDENCE_FILE_SIZE_REQUIRED`
- `EVIDENCE_FILE_SIZE_INVALID`
- `EVIDENCE_FILE_TOO_LARGE`
- `EVIDENCE_FILE_EXTENSION_INVALID`

Estos codigos permiten que Compose/ViewModel muestre errores especificos sin depender de texto libre.

## 7. Datos semilla agregados

Se agregaron:
- 4 valoraciones estructuradas.
- 9 relaciones `RatingQuality`.
- 1 evidencia opcional valida.

Los datos respetan:
- usuario existente,
- negocio existente,
- cualidades del catalogo oficial,
- categorias aplicables,
- precio con fecha,
- campos estructurados sin comentarios libres.

## 8. Pruebas agregadas o reforzadas

- `EvidenceValidatorTest`
- Valida evidencia vacia como opcion permitida.
- Valida imagen permitida.
- Valida PDF permitido.
- Rechaza tamano faltante.
- Rechaza archivo mayor a 5 MB.
- Rechaza extension incompatible con tipo seleccionado.

- `SeedCatalogDataTest`
- Verifica que las valoraciones semilla referencien usuarios y negocios existentes.
- Verifica que las cualidades asociadas referencien valoraciones y cualidades existentes.
- Verifica que la evidencia semilla referencie una valoracion existente.

- `RatingRegistrationManagerTest`
- Refuerza registro de evidencia con `fileSizeBytes`.
- Verifica rechazo de evidencia invalida sin guardar valoracion.

- `ContractMappersTest`
- Verifica que el contrato del formulario exponga la politica de evidencia.
- Verifica que `EvidenceRequestDto` se mapee con `fileSizeBytes`.

## 9. Requerimientos cubiertos

- RF-18: evidencia opcional con validacion controlada.
- RNF-11: documentacion tecnica del modulo.
- RNF-12: estructura mantenible mediante politica centralizada y DTOs claros.
- RNF-15: validacion antes de almacenar.
- RNF-16: errores estructurados para control de acceso y consumo desde frontend.

## 10. Checklist final del modulo backend

- Entidades base definidas.
- Relaciones principales modeladas.
- Catlogos estructurados definidos.
- Valoraciones sin texto libre protegidas desde logica.
- Validaciones de rangos y catalogos implementadas.
- Registro de usuarios implementado.
- Login basico implementado.
- Negocios importados desde Google Places vinculados automaticamente a categorias.
- Registro de valoraciones estructuradas implementado.
- Consultas, filtros e historial implementados.
- Metricas, porcentajes, rangos y rankings implementados.
- Contratos DTO para frontend implementados.
- Evidencia opcional validada.
- Datos semilla coherentes agregados.
- Pruebas unitarias reforzadas.

## 11. Pendientes fuera de esta fase

- Integracion visual completa en Compose para carga real de archivos.
- Definir almacenamiento final de evidencia: local privado, backend remoto o proveedor externo.
- Politica de privacidad para fotografias de facturas o cotizaciones.
- Control de permisos de archivos en Android si se habilita seleccion desde galeria o camara.
