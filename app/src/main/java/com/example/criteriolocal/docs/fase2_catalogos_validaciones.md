# CriterioLocal - Fase 2 catalogos y validaciones base

## 1. Resumen de la fase

La Fase 2 cierra las opciones estructuradas permitidas por el sistema y agrega la primera capa formal de validacion de valoraciones.

El objetivo fue proteger desde la logica de dominio las reglas principales del proyecto:

- no aceptar texto libre en valoraciones
- usar solo escalas y opciones predefinidas
- exigir precio con fecha
- asociar toda valoracion a usuario y negocio
- validar cualidades contra catalogo oficial
- rechazar registros incompletos o inconsistentes

No se implementaron consultas avanzadas, rankings, resumenes ni comparaciones.

## 2. Catalogo estructurado del sistema

Archivo principal:

```text
app/src/main/java/com/example/criteriolocal/domain/catalog/AppCatalogs.kt
```

### Escala de calificacion

La escala queda cerrada de 1 a 5:

```text
1, 2, 3, 4, 5
```

Aplica a:

- calificacion de servicio
- calificacion de atencion
- nivel de satisfaccion

### Tiempo de espera

Opciones:

- hasta 15 minutos
- de 15 a 30 minutos
- de 31 a 60 minutos
- mas de 60 minutos

Modelo:

```text
WaitTimeOption
```

### Frecuencia de uso

Opciones:

- primera vez
- uso ocasional
- uso frecuente
- uso regular

Modelo:

```text
UsageFrequencyOption
```

### Disponibilidad

Opciones:

- no disponible
- disponibilidad limitada
- disponible

Modelo:

```text
AvailabilityOption
```

### Tipo de atencion

Opciones:

- presencial
- visita a domicilio
- entrega
- en linea

Modelo:

```text
ServiceModeOption
```

### Cualidades oficiales

Catalogo base:

- Atencion rapida
- Trato amable
- Precio accesible
- Higiene adecuada
- Buena explicacion del servicio
- Puntualidad
- Variedad de productos
- Servicio confiable
- Diagnostico claro
- Repuestos disponibles

Las cualidades pueden ser generales o aplicar a una categoria especifica.

Ejemplos:

- `Variedad de productos` aplica a categoria `2` Farmacia.
- `Diagnostico claro` aplica a categoria `5` Clinica medica.
- `Repuestos disponibles` aplica a categoria `4` Taller mecanico.

## 3. Validadores y reglas implementadas

Archivos principales:

```text
app/src/main/java/com/example/criteriolocal/domain/validation/RatingValidationModels.kt
app/src/main/java/com/example/criteriolocal/domain/validation/RatingValidator.kt
```

### Modelos de validacion

`RatingValidationRequest` representa una valoracion antes de ser aceptada por el sistema.

Sus campos son nullable porque simula entrada de formulario y permite detectar faltantes antes de construir el modelo definitivo `Rating`.

Campos clave:

- `userId`
- `businessId`
- `ratedOn`
- `reportedPrice`
- `priceReportedOn`
- `serviceScore`
- `attentionScore`
- `satisfactionScore`
- `waitTime`
- `wouldRecommend`
- `usageFrequency`
- `availability`
- `serviceMode`
- `selectedQualityIds`
- `freeTextComment`

`freeTextComment` existe solo para detectar y rechazar intentos de texto libre. No forma parte del modelo persistente de valoracion.

### Contexto de validacion

`RatingValidationContext` recibe:

- usuarios existentes
- negocios existentes con su categoria
- cualidades oficiales
- escala permitida
- precio minimo y maximo permitido

Esto permite validar relaciones y catalogos sin acoplar el dominio a Room.

### Resultado de validacion

`ValidationResult` devuelve:

- `isValid`
- lista de `ValidationError`
- busqueda por `ValidationErrorCode`

Cada error incluye:

- codigo estructurado
- campo afectado
- mensaje para UI o logs

## 4. Reglas cubiertas por el validador

El validador rechaza:

- comentarios de texto libre
- valoraciones sin usuario
- usuario inexistente
- valoraciones sin negocio
- negocio inexistente
- fecha de valoracion vacia o con formato invalido
- precio vacio
- precio fuera de rango
- fecha de precio vacia o con formato invalido
- calificaciones vacias
- calificaciones fuera de escala 1 a 5
- tiempo de espera vacio
- recomendacion vacia
- frecuencia de uso vacia
- disponibilidad vacia
- tipo de atencion vacio
- ausencia de cualidades
- cualidades duplicadas
- cualidades inexistentes en catalogo
- cualidades inactivas
- cualidades que no aplican a la categoria del negocio

## 5. Pruebas unitarias agregadas

### Catalogos

Archivo:

```text
app/src/test/java/com/example/criteriolocal/domain/catalog/AppCatalogsTest.kt
```

Valida:

- escala cerrada de 1 a 5
- cobertura completa de enums en catalogos
- cualidades oficiales con IDs unicos, nombre y descripcion

### Validadores

Archivo:

```text
app/src/test/java/com/example/criteriolocal/domain/validation/RatingValidatorTest.kt
```

Valida:

- una valoracion estructurada completa es aceptada
- texto libre es rechazado
- escalas fuera de rango son rechazadas
- precio invalido y fecha invalida son rechazados
- usuario y negocio inexistentes son rechazados
- campos obligatorios faltantes son rechazados
- cualidades duplicadas, inactivas, inexistentes o no aplicables son rechazadas

## 6. Requerimientos cubiertos

### RF cubiertos

- `RF-09`: valoracion usando datos definidos por la app
- `RF-10`: precio reportado
- `RF-11`: calificacion de servicio en escala 1 a 5
- `RF-12`: calificacion de atencion en escala 1 a 5
- `RF-13`: tiempo de espera con intervalos predefinidos
- `RF-14`: nivel de satisfaccion en escala definida
- `RF-15`: recomendaria o no recomendaria
- `RF-16`: cualidades predefinidas
- `RF-17`: precio con fecha
- `RF-19`: bloqueo de valoraciones incompletas o inconsistentes

### RN cubiertas

- `RN-01`: no se permiten comentarios abiertos
- `RN-02`: toda valoracion usa opciones predefinidas
- `RN-03`: criterios claros, comparables y medibles
- `RN-06`: toda valoracion se asocia a usuario y negocio
- `RN-07`: valores validos segun catalogos y escalas
- `RN-08`: precios con fecha
- `RN-09`: cualidades dentro del catalogo oficial
- `RN-10`: rechazo de registros incompletos o inconsistentes

### RLD cubiertos

- `RLD-02`: validaciones de integridad y consistencia
- `RLD-03`: reglas de negocio contra entradas fuera de criterios definidos

## 7. Dependencias para la siguiente fase

Para la siguiente fase ya queda disponible:

- catalogos cerrados para construir formularios
- request de validacion para entrada de datos
- contexto de validacion para usuarios, negocios y cualidades
- errores estructurados para mostrar mensajes claros
- validador desacoplado de UI y Room

Pendientes para fase posterior:

- conectar el validador con el formulario real de valoracion
- guardar valoraciones solo si `ValidationResult.isValid` es `true`
- mapear errores a textos de pantalla
- decidir si la seleccion de al menos una cualidad sera obligatoria definitivamente
- definir si se validara fecha futura con reglas de negocio adicionales
