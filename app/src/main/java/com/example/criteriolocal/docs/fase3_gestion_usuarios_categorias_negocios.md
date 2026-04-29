# Fase 3. Gestion de usuarios, categorias y negocios

## 1. Resumen de la fase

Esta fase implementa la base operativa del sistema para registrar usuarios, autenticar usuarios, consultar perfil basico, registrar categorias, registrar negocios y consultar negocios por listado, categoria, busqueda por nombre y detalle.

La implementacion mantiene el alcance cerrado de la fase:

- No registra valoraciones completas.
- No calcula promedios.
- No genera rankings.
- No implementa comparaciones avanzadas.
- No agrega comentarios abiertos ni campos de opinion libre.

## 2. Logica implementada

### Usuarios

- `UserManager.registerUser` valida nombre, correo, contrasena y duplicados.
- El correo se normaliza con `trim` y minusculas.
- La contrasena no se guarda en texto plano; se almacena como hash SHA-256.
- `UserManager.login` valida credenciales y estado activo.
- `UserManager.getBasicProfile` devuelve usuario y cantidad de valoraciones asociadas.
- `UserManager.observeUserHistory` deja lista la estructura para historial de valoraciones.

### Categorias

- `BusinessCategoryManager.registerCategory` permite crear categorias.
- Rechaza nombre vacio.
- Rechaza categorias duplicadas por nombre sin distinguir mayusculas/minusculas.
- La persistencia local permite IDs autogenerados.

### Negocios

- `BusinessCategoryManager.registerBusiness` permite crear negocios asociados a una categoria existente.
- Valida nombre, direccion y categoria.
- Guarda datos basicos del negocio: nombre, descripcion, direccion, telefono, Google Place ID y coordenadas cuando existan.
- Deja disponible la consulta general, busqueda por nombre, filtro por categoria y detalle basico.

## 3. Archivos creados o modificados

### Creados

- `domain/security/PasswordHasher.kt`
- `domain/management/ManagementResult.kt`
- `domain/management/UserManagementModels.kt`
- `domain/management/BusinessManagementModels.kt`
- `domain/management/UserManager.kt`
- `domain/management/BusinessCategoryManager.kt`
- `src/test/.../domain/management/UserManagerTest.kt`
- `src/test/.../domain/management/BusinessCategoryManagerTest.kt`
- `src/test/.../domain/management/FakeRatings.kt`
- `docs/fase3_gestion_usuarios_categorias_negocios.md`

### Modificados

- `domain/repository/RepositoryInterfaces.kt`
- `data/local/dao/Daos.kt`
- `data/local/entity/Entities.kt`
- `data/local/database/CriterioLocalDatabase.kt`
- `data/repository/DefaultRepositories.kt`
- `core/di/AppContainer.kt`
- `src/test/.../ui/home/HomeViewModelTest.kt`

## 4. Validaciones implementadas

### Registro de usuario

- Nombre obligatorio.
- Correo obligatorio.
- Formato basico de correo valido.
- Contrasena obligatoria.
- Contrasena minima de 8 caracteres.
- Correo unico.

### Login

- Correo obligatorio y con formato valido.
- Contrasena obligatoria.
- Credenciales validas.
- Usuario activo.

### Categorias

- Nombre de categoria obligatorio.
- Nombre de categoria no duplicado.

### Negocios

- Nombre obligatorio.
- Direccion obligatoria.
- Categoria obligatoria.
- Categoria existente antes de guardar el negocio.

## 5. Requerimientos cubiertos

### RF

- RF-01: registro de usuarios.
- RF-02: inicio de sesion de usuarios registrados.
- RF-03: perfil basico del usuario.
- RF-04: estructura para historial de valoraciones del usuario.
- RF-05: registro de negocios locales.
- RF-06: clasificacion de negocios por categorias.
- RF-07: consulta de informacion general de negocio.
- RF-08: consulta por nombre o categoria.

### RLD

- RLD-01: almacenamiento de usuarios, negocios y categorias.
- RLD-04: base de consultas por nombre y categoria.

### RN relacionadas

- RN-01: no se agrego texto libre en valoraciones.
- RN-02: la fase conserva el enfoque de opciones estructuradas.
- RN-06: se mantiene asociacion de valoraciones futura con usuario y negocio.
- RN-10: se rechazan registros incompletos en usuarios, categorias y negocios.

## 6. Pruebas agregadas

### `UserManagerTest`

- Verifica que el registro normaliza correo y guarda hash de contrasena.
- Verifica rechazo de correos duplicados.
- Verifica login correcto y rechazo de contrasena incorrecta.
- Verifica perfil basico con conteo de valoraciones.

### `BusinessCategoryManagerTest`

- Verifica alta de categoria y rechazo de duplicados.
- Verifica rechazo de negocio con categoria inexistente.
- Verifica alta de negocio asociado a categoria.
- Verifica busqueda general cuando el texto de busqueda esta vacio.

## 7. Dependencias para la siguiente fase

- La Fase 4 podra usar `UserManager.observeUserHistory` para enlazar historial real cuando se implemente el registro completo de valoraciones.
- La Fase 4 debe conectar `RatingValidator` con persistencia para guardar valoraciones estructuradas.
- Las metricas, rankings y comparaciones deben seguir sin implementarse hasta la fase correspondiente.
- Si el equipo decide autenticacion remota, `UserManager` queda como punto unico para reemplazar la fuente de datos sin romper la interfaz.
