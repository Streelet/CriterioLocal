# Fase 4. Registro de valoraciones estructuradas

## 1. Resumen de la fase

Esta fase implementa el registro completo de valoraciones estructuradas, sin comentarios libres y con validaciones obligatorias antes de persistir.

La valoracion permite registrar:

- usuario asociado,
- negocio asociado,
- fecha de valoracion,
- precio reportado,
- fecha del precio,
- calificacion de servicio de 1 a 5,
- calificacion de atencion de 1 a 5,
- nivel de satisfaccion de 1 a 5,
- tiempo de espera por catalogo,
- recomendacion si/no,
- frecuencia de uso por catalogo,
- disponibilidad por catalogo,
- tipo de atencion por catalogo,
- multiples cualidades oficiales,
- evidencia opcional desacoplada.

No se implementan consultas avanzadas, metricas, rankings ni comparaciones.

## 2. Flujo logico del registro

1. El frontend construye un `RegisterStructuredRatingRequest` con campos cerrados.
2. `RatingRegistrationManager` consulta usuario, negocio y cualidades oficiales.
3. Se construye un `RatingValidationContext` con IDs existentes y categoria del negocio.
4. `RatingValidator` valida campos obligatorios, escalas, precio, fechas, usuario, negocio y cualidades.
5. `EvidenceValidator` valida evidencia solo si se envia.
6. Si hay errores, no se guarda nada.
7. Si todo es valido, `RatingRepository.saveStructuredRating` guarda en transaccion:
   - valoracion,
   - cualidades vinculadas,
   - evidencias opcionales.

## 3. Archivos creados o modificados

### Creados

- `domain/management/RatingRegistrationModels.kt`
- `domain/management/RatingRegistrationManager.kt`
- `domain/validation/EvidenceValidator.kt`
- `src/test/.../domain/management/RatingRegistrationManagerTest.kt`
- `docs/fase4_registro_valoraciones_estructuradas.md`

### Modificados

- `domain/validation/RatingValidationModels.kt`
- `domain/repository/RepositoryInterfaces.kt`
- `data/repository/DefaultRepositories.kt`
- `core/di/AppContainer.kt`

## 4. Validadores y reglas implementadas

- No acepta texto libre en valoraciones.
- No acepta usuario nulo, invalido o inexistente.
- No acepta negocio nulo, invalido o inexistente.
- No acepta precio vacio, negativo o fuera del rango oficial.
- Exige fecha del precio.
- Exige fecha de valoracion.
- Valida formato basico `YYYY-MM-DD`.
- Exige calificaciones dentro de escala 1 a 5.
- Exige tiempo de espera desde catalogo.
- Exige recomendacion si/no.
- Exige frecuencia de uso desde catalogo.
- Exige disponibilidad desde catalogo.
- Exige tipo de atencion desde catalogo.
- Exige al menos una cualidad.
- Rechaza cualidades duplicadas.
- Rechaza cualidades fuera del catalogo oficial.
- Rechaza cualidades inactivas.
- Rechaza cualidades que no aplican a la categoria del negocio.
- La evidencia es opcional.
- Si se envia evidencia, valida ruta, tipo y fecha de carga.

## 5. Requerimientos cubiertos

### RF

- RF-09: registrar valoracion solo con datos definidos por la app.
- RF-10: registrar precio pagado o cotizado.
- RF-11: calificacion del servicio en escala 1 a 5.
- RF-12: calificacion de atencion en escala 1 a 5.
- RF-13: tiempo de espera con intervalos predefinidos.
- RF-14: nivel de satisfaccion.
- RF-15: recomendacion si/no.
- RF-16: seleccion de cualidades predefinidas.
- RF-17: precios con fecha.
- RF-18: evidencia opcional desacoplada.
- RF-19: bloqueo de valoraciones incompletas o inconsistentes.

### RN

- RN-01: no se permiten comentarios abiertos.
- RN-02: toda valoracion usa opciones predefinidas.
- RN-03: criterios claros, comparables y medibles.
- RN-06: toda valoracion se asocia a usuario y negocio.
- RN-07: solo se aceptan valores validos segun catalogos y escalas.
- RN-08: precios con fecha.
- RN-09: cualidades del catalogo oficial.
- RN-10: no se aceptan registros incompletos o inconsistentes.

### RLD

- RLD-02: validaciones para integridad y consistencia.
- RLD-03: reglas de negocio para impedir entradas fuera de criterios definidos.

## 6. Pruebas agregadas

`RatingRegistrationManagerTest` cubre:

- registro exitoso con valoracion, cualidades y evidencia,
- registro exitoso sin evidencia,
- rechazo de texto libre,
- rechazo de cualidad fuera del catalogo,
- rechazo de usuario y negocio inexistentes,
- rechazo de evidencia invalida sin guardar valoracion.

## 7. Pendientes para consultas y metricas

- Consultar historial de valoraciones por usuario.
- Consultar valoraciones por negocio.
- Calcular promedios de servicio, atencion y satisfaccion.
- Calcular porcentaje de recomendacion.
- Calcular rango y promedio de precios.
- Calcular cualidades mas seleccionadas.
- Generar rankings por categoria.
