# CriterioLocal - Fase 1 base hibrida y pruebas

## 1. Objetivo de lo implementado

En esta etapa se dejo lista la base tecnica de la aplicacion para:

- manejar datos locales con Room
- consumir negocios cercanos desde Google Places Nearby Search
- mantener una arquitectura separada por responsabilidades
- preparar la aplicacion para fases posteriores de valoracion estructurada
- validar el comportamiento actual con pruebas unitarias e integracion real

No se implemento todavia la logica completa de registro de valoraciones, metricas, rankings ni comparaciones avanzadas.

## 2. Arquitectura actual

La app esta organizada en 4 capas:

- `core`: inicializacion e inyeccion simple de dependencias
- `domain`: modelos y contratos de negocio
- `data`: persistencia local, consumo remoto, mapeos y repositorios
- `ui`: pantalla de inicio y estado visible para Compose

Estructura principal:

```text
com.example.criteriolocal
├── core/di
├── data
│   ├── catalog
│   ├── local
│   ├── remote
│   ├── mapper
│   └── repository
├── domain
│   ├── catalog
│   ├── model
│   └── repository
└── ui
    ├── app
    ├── home
    └── theme
```

## 3. Modelo actual

Entidades base:

- `User`
- `Category`
- `Business`
- `Rating`
- `Quality`
- `RatingQuality`
- `Evidence`

Detalles importantes:

- `Rating` no tiene campo de comentario libre
- `Rating` exige usuario, negocio, precio y fecha de precio
- `Business` ahora soporta:
  - `googlePlaceId`
  - `latitude`
  - `longitude`
- las cualidades siguen saliendo de catalogo oficial

## 4. Persistencia local

Se usa `Room` como base local.

Componentes principales:

- entidades Room en `data/local/entity`
- relaciones Room en `data/local/relation`
- DAOs en `data/local/dao`
- base de datos en `data/local/database`
- mapeos dominio <-> entidad en `data/mapper`

La base esta en version `2` y usa `fallbackToDestructiveMigration()` porque seguimos en fase base y el esquema esta cambiando rapido.

## 5. Consumo remoto

Se agrego una capa remota con:

- `Retrofit`
- `Kotlin Serialization`
- DTOs para `Nearby Search`
- repositorio remoto `DefaultRemotePlaceRepository`

Endpoint usado:

```text
GET maps/api/place/nearbysearch/json
```

Consulta base usada en la app:

- latitud: `14.7906`
- longitud: `-89.5447`
- radio: `2000`
- tipo: `pharmacy`

Eso hace que la pantalla inicial consulte farmacias cercanas a Chiquimula al cargar.

## 6. Manejo de la API key

La clave no se guarda en el codigo.

Se usa la variable de entorno:

```text
GOOGLE_PLACES_API_KEY
```

Gradle la inyecta en:

- `BuildConfig.GOOGLE_PLACES_API_KEY`
- `BuildConfig.GOOGLE_PLACES_BASE_URL`

Comando para registrar la variable en PowerShell:

```powershell
[System.Environment]::SetEnvironmentVariable('GOOGLE_PLACES_API_KEY','TU_CLAVE','User')
```

Si Android Studio ya estaba abierto cuando se creo la variable, conviene reiniciar Android Studio y volver a sincronizar Gradle.

## 7. Comportamiento actual de la pantalla inicial

La pantalla `HomeScreen` muestra:

- resumen de la arquitectura base
- aviso etico
- catalogos cerrados
- categorias locales
- negocios demo consumidos desde la API
- negocios locales base
- cualidades oficiales
- usuarios semilla

Si la API responde correctamente, la tarjeta `Negocios demo consumidos desde la API` muestra negocios reales de Google Places.

Si la API falla, la pantalla:

- muestra el error remoto
- mantiene visibles los negocios locales base

## 8. Pruebas implementadas

### 8.1 Prueba de integracion real

Archivo:

```text
app/src/test/java/com/example/criteriolocal/data/remote/GooglePlacesNearbySearchIntegrationTest.kt
```

Que valida:

- que la API key este configurada
- que Google Places responda con `status = OK`
- que se reciban negocios cercanos
- que cada negocio tenga `placeId` y `name`
- que al menos uno tenga tipo `pharmacy`
- que las coordenadas devueltas caigan en el area esperada de Chiquimula

### 8.2 Prueba de mapper remoto

Archivo:

```text
app/src/test/java/com/example/criteriolocal/data/remote/RemotePlaceMappersTest.kt
```

Que valida:

- que un DTO valido se convierta correctamente al modelo de dominio
- que respuestas incompletas no rompan el flujo
- que campos opcionales nulos sigan siendo nulos sin causar errores

### 8.3 Prueba del repositorio remoto

Archivo:

```text
app/src/test/java/com/example/criteriolocal/data/repository/DefaultRemotePlaceRepositoryTest.kt
```

Que valida:

- que el repositorio formatee bien `location=lat,lng`
- que envie `radius`, `type` y `apiKey` correctamente
- que falle rapido si la API key esta vacia

### 8.4 Prueba del ViewModel de inicio

Archivo:

```text
app/src/test/java/com/example/criteriolocal/ui/home/HomeViewModelTest.kt
```

Que valida:

- que se ejecute el bootstrap local
- que el estado local se cargue correctamente
- que los negocios remotos lleguen al `uiState`
- que si la API falla, la UI mantenga datos locales y reporte el error remoto

## 9. Explicacion de GooglePlacesNearbySearchIntegrationTest

Esta prueba es una prueba de integracion real, no una prueba unitaria.

Su objetivo es comprobar que la cadena completa funciona:

1. se crea el cliente Retrofit real
2. se crea el repositorio remoto real
3. se llama a Google Places con coordenadas de Chiquimula
4. se valida que la respuesta venga bien estructurada

Puntos importantes del test:

- Usa `assumeTrue(...)` para no fallar si la clave no esta configurada.
- Si la clave existe, entonces si ejecuta la llamada real.
- Comprueba `status == "OK"` para verificar que Google acepto la consulta.
- Comprueba que `places` no este vacio para evitar falsos positivos.
- Comprueba `googlePlaceId` y `name` para validar identidad minima del negocio.
- Comprueba tipos y coordenadas para confirmar que los datos pertenecen al contexto esperado.

Por que este test es importante:

- detecta problemas de configuracion de clave
- detecta cambios en el endpoint o contrato remoto
- detecta respuestas vacias o fuera de zona
- confirma que Retrofit + DTOs + mapper + repositorio siguen funcionando juntos

Limitaciones del test:

- depende de internet
- depende de cuota y disponibilidad del servicio externo
- puede variar la cantidad exacta de negocios porque Google Places cambia con el tiempo

Por eso se usa junto a pruebas unitarias locales, no en lugar de ellas.

## 10. Comandos utiles

Ejecutar pruebas locales y de integracion:

```powershell
$env:GOOGLE_PLACES_API_KEY = [System.Environment]::GetEnvironmentVariable('GOOGLE_PLACES_API_KEY','User')
.\gradlew.bat testDebugUnitTest
```

## 11. Pendientes tecnicos

- definir si los negocios remotos se guardaran en Room como cache
- definir mapeo formal de tipos de Google Places a categorias internas
- decidir si se usara `Place Details` para enriquecer telefono, horarios y otros datos
- decidir si la pantalla de inicio debe permitir cambiar tipo y radio de busqueda

## 12. Conclusiones

La Fase 1 ya no es solo local. Ahora la app tiene:

- base local con Room
- consumo remoto real con Google Places
- estado de UI que integra ambas fuentes
- pruebas unitarias e integracion
- documentacion tecnica suficiente para que el equipo continue sin ambiguedad
