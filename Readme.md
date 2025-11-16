# Trabajo Integrador Final

Proyecto desarrollado en **Java** con **Spring Boot** para gestionar una biblioteca, con usuarios, libros y préstamos. Incluye manejo de roles, encriptación de contraseñas y carga inicial de datos.

---

## Tecnologías

- Java 17+
- Spring Boot 3.5.7
- Spring Security
- Spring Data / JDBC
- MySQL 8+
- JUnit 5
- Mockito
- Maven / Gradle
- H2 (para tests en memoria)

---

## Estructura del proyecto

src/ <br>
├─ main/ <br>
│ ├─ java/<br>
│ │ └─ utn/tif/trabajo_integrador_final/<br>
│ │ ├─ config/ ==> Configuración de DB, bootstrap y seguridad<br>
│ │ ├─ controllers/ ==> Controladores REST<br>
│ │ ├─ DAOS/ ==> Acceso a datos<br>
│ │ ├─ DTOs/ ==> Objetos de transferencia de datos<br>
│ │ ├─ models/ ==> Entidades y enums<br>
│ │ ├─ services/ ==> Lógica de negocio<br>
│ │ └─ utils/ ==> Utilidades (ej: carga inicial de datos)<br>
│ └─ resources/<br>
│ ├─ application.properties<br>
│ ├─ application-test.properties<br>
│ └─ sqlScripts ==> Scripts para crear la DB y cargar los datos de prueba<br>
└─ test/ <br>
└─ java/<br>
└─ utn/tif/trabajo_integrador_final/<br>
├─ unitary/ ==> Tests unitarios<br>
└─ integration/ ==> Tests de integración

---
## Funcionalidades

- Pool de conexiones utilizando Hikari
- Transaction manager customizado para la gestión de transacciones acorde a lo solicitado en el proyecto
- API completa y funcional que permite:<br>
  - CRUD completo de Usuarios, Libros y Préstamos
  - Baja lógica y física para Usuarios y Libros
  - Carga automática de datos de prueba al correr el proyecto
  - Manejo de errores y excepciones con sus respectivos Códigos de estatus HTTP en las respuestas
  - Simulación de Login con alerta de seguridad para cambiar la contraseña cada 30 días
- Tests Unitarios por capa
- Tests de Integración por Controller

---

## Instrucciones de Uso

### Por IDE
1. Abrir el proyecto con un IDE que permita el uso de consola(VSCode, IntelliJ, NetBeans)
2. Abrir la consola (Debería abrirse en el directorio raíz del proyecto, en caso de no ser así, ir con cd hasta el mismo)
3. Ejecutar el comando:
    ```bash 
    ./mvnw clean install
    ``` 
4. Luego dependiendo de lo que se desee hacer:
   - Para correr los test ejecutar:
   ```bash
   ./mvnw test
   ```
   - Para correr la API:
   ```bash
   ./mvnw spring-boot::run
   ```

### Por consola
1. Abrir la consola desde el directorio raíz del proyecto o navegar hasta el mismo
2. Ejecutar el comando:
    ```bash 
    ./mvnw clean install
    ``` 
3. Luego dependiendo de lo que se desee hacer:
    - Para correr los test ejecutar:
   ```bash
   ./mvnw test
   ```
    - Para correr la API:
   ```bash
   ./mvnw spring-boot::run
   ```
---
## Datos de prueba

### Libros

No se listan porque se crea un lote de 5000 libros ficticios al inicializar el proyecto con un procedure sql

### Usuarios

| Usuario | Email | Rol         |
|---------|-------|-------------|
| Admin Principal | admin@tif.com | ADMIN, USER |
| Juan Pérez | juan@example.com | USER        |
| Maria Gomez | maria@example.com | USER        |
| Ana Librera | bibliotecaria@tif.com | ADMIN, USER |


## Endpoints y colección de prueba en postman

### Configuración Inicial
Desde postman cargar la colección, la misma se encuentra en:<br>

Directorio raíz -> main -> resources -> TIF_P2.postman_collection.json

### Variables de Entorno
```bash
# En Postman, configurar las variables:
base_url: http://localhost:8080/api
```

### Prerrequisitos
- Servidor ejecutándose en `http://localhost:8080`
- Base de datos configurada y corriendo
- Dependencias del proyecto instaladas

---

## MÓDULO USUARIOS (USER)

### Obtener Todos los Usuarios
**GET** `{{base_url}}/users`
- Retorna lista completa de usuarios

### Obtener Usuario por ID
**GET** `{{base_url}}/users/:id`
- **Parámetro**: `id` - ID del usuario
- **Ejemplo**: `5a75fdfc-99ff-4fd7-82db-2f9f657c96b6_202511091941`

### Buscar Múltiples Usuarios
**GET** `{{base_url}}/users/many`
- **Body**: Array de IDs de usuarios
```json
["5939b6eb-9ed3-4ac0-8175-079545b02f45_202511092240", "5cfcef4f-a24f-4807-8ceb-d43b89236763_202511092240"]
```

### Crear Nuevo Usuario
**POST** `{{base_url}}/users/`
```json
{
  "nombre": "Emanuel",
  "apellido": "Gómez",
  "email": "emanuel@example.com",
  "rol": "USER",
  "password": "contraseña123",
  "fechaNac": "1995-08-24"
}
```

### Creación Masiva de Usuarios
**POST** `{{base_url}}/users/bulk`
- **Body**: 
  ```json
  [
    {
    "nombre": "Emanuel",
    "apellido": "Gómez",
    "email": "emanuel@example.com",
    "rol": "USER",
    "password": "contraseña123",
    "fechaNac": "1995-08-24"
    },
    {
    "nombre": "Emilia",
    "apellido": "Gómez",
    "email": "emilia@example.com",
    "rol": "USER",
    "password": "contraseña123",
    "fechaNac": "2001-01-04"
    },
    {
    "nombre": "Matias",
    "apellido": "Farfan",
    "email": "matias@example.com",
    "rol": "USER",
    "password": "contraseña123",
    "fechaNac": "1995-08-24"
    },
    {
    "nombre": "Marianela",
    "apellido": "Guerrero",
    "email": "marianela@example.com",
    "rol": "USER",
    "password": "contraseña123",
    "fechaNac": "1995-08-24"
    }
  ]
    ```

### Autenticación Básica
**POST** `{{base_url}}/users/login`
```json
{
  "email": "laura.martinez@example.com",
  "password": "password456"
}
```

### Actualizar Usuario
**PUT** `{{base_url}}/users/:id`
```json
{
  "nombre": "Emanuel",
  "roles": ["USER"]
}
```

### Actualización Masiva
**PUT** `{{base_url}}/users/batch`
- **Body**:
```json
[
  {
    "id": id del Usuario
    "nombre": "Emanuel",
    "roles": ["USER","ADMIN"]
  },
  {
    "id": id del Usuario
    "nombre": "EMILIA",
    "roles": ["USER","ADMIN"]
  },
  {
    "id": id del Usuario
    "nombre": "MATIAS",
    "roles": ["USER","ADMIN"]
  },
  {
    "id": id del Usuario
    "nombre": "MARIANELA",
    "roles": ["USER","ADMIN"]
  }
]
```

### Eliminación Lógica
**DELETE** `{{base_url}}/users/:id`
- Eliminación suave (soft delete)

### 🔹 Eliminación Masiva Lógica
**DELETE** `{{base_url}}/users/batch`
- **Body**: Array de IDs para eliminar

### 🔹 Eliminación Física
**DELETE** `{{base_url}}/users/abs/:id`
- Eliminación permanente

### 🔹 Eliminación Masiva Física
**DELETE** `{{base_url}}/users/abs`
- **Body**: Array de IDs para eliminación permanente

---

## MÓDULO LIBROS (LIBROS)

### Obtener Todos los Libros
**GET** `{{base_url}}/libros`

### Buscar Libro por ID
**GET** `{{base_url}}/libros/:id`
- **Ejemplo**: `297db194-b2c9-11f0-a30a-08bfb81ce7ec`

### Buscar Múltiples Libros
**GET** `{{base_url}}/libros/many`
- **Body**: Array de IDs de libros

### Crear Nuevo Libro
**POST** `{{base_url}}/libros`
```json
{
  "isbn": "978-950-515-123-4",
  "titulo": "El nombre del viento",
  "autor": "Patrick Rothfuss",
  "editorial": "Plaza & Janés",
  "anioEdicion": 2009,
  "clasificacionDewey": "813.6",
  "estanteria": "Fantasía A1",
  "idioma": "Español",
  "existencias": 5,
  "disponibles": 5,
  "eliminado": false
}
```

### Creación Masiva de Libros
**POST** `{{base_url}}/libros/bulk`
- **Body**: Array de Libros

### Actualizar Libro
**PUT** `{{base_url}}/libros/:id`
```json
{
  "titulo": "prueba de update"
}
```

### Actualización Masiva de Libros
**PUT** `{{base_url}}/libros/bulk`
- **Body**: Array de libros a actualizar

### Eliminación Lógica
**DELETE** `{{base_url}}/libros/:id`

### Eliminación Masiva Lógica
**DELETE** `{{base_url}}/libros/bulk`
- **Body**: Array de IDs para eliminación lógica

### Eliminación Física
**DELETE** `{{base_url}}/libros/:id`
- Eliminación permanente

### Eliminación Masiva Física
**DELETE** `{{base_url}}/libros/total`
- **Body**: Array de IDs para eliminación permanente

---

## MÓDULO PRÉSTAMOS (PRESTAMOS)

### Obtener Todos los Préstamos
**GET** `{{base_url}}/prestamos`
- Devuelve un array de libros

### Obtener Préstamo por ID
**GET** `{{base_url}}/prestamos/:id`
- **Ejemplo**: ID `1`

### Obtener Préstamos por Usuario
**GET** `{{base_url}}/prestamos/user/:uid`
- **Parámetro**: `uid` - ID del usuario

### Obtener Préstamos por Libro
**GET** `{{base_url}}/prestamos/libro/:lid`
- **Parámetro**: `lid` - ID del libro

### Obtener Préstamos por Estado
**GET** `{{base_url}}/prestamos/estado/:estado`
- **Estados**: `activo`, `devuelto`, `vencido`

### Obtener Préstamos Vencidos
**GET** `{{base_url}}/prestamos/vencidos`

### Obtener Múltiples Préstamos
**GET** `{{base_url}}/prestamos/many`
- **Body**: Array de IDs de préstamos

### Crear Nuevo Préstamo
**POST** `{{base_url}}/prestamos`
```json
{
  "libroId": "297cf2f0-b2c9-11f0-a30a-08bfb81ce7ec",
  "userId": "af8f66c5-5a70-48f0-90e4-0373699efd74_202511092240"
}
```

### Creación Masiva de Préstamos
**POST** `{{base_url}}/prestamos/bulk`
- **Body**: Array de préstamos a crear

### Actualizar Plazo de Préstamo
**PUT** `{{base_url}}/prestamos/:id/plazo?newPlazo=2025-06-10`
- **Query**: `newPlazo` - Nueva fecha de plazo

### Actualizar Estado de Préstamos
**PUT** `{{base_url}}/prestamos/actualizar-estado`
- Actualiza estados automáticamente (vencidos, etc.)

### Actualizar Préstamo
**PUT** `{{base_url}}/prestamos/:id`
```json
{
  "fechaDevolucion": "2025-11-11"
}
```

### Actualización Masiva de Préstamos
**PUT** `{{base_url}}/prestamos/bulk`
- **Body**: Array de préstamos a actualizar

### Eliminar Préstamo
**DELETE** `{{base_url}}/prestamos/:id`

### Eliminación Masiva de Préstamos
**DELETE** `{{base_url}}/prestamos/bulk`
- **Body**: Array de IDs de préstamos a eliminar

---

## Flujos de Trabajo Típicos

### 1. Registro y Autenticación de Usuario
1. Crear usuario → `POST /users`
2. Login → `POST /users/login`

### 2. Gestión de Libros
1. Crear libros → `POST /libros/bulk`
2. Listar libros disponibles → `GET /libros`
3. Actualizar información → `PUT /libros/:id`

### 3. Proceso de Préstamo
1. Crear préstamo → `POST /prestamos`
2. Ver préstamos activos → `GET /prestamos/estado/activo`
3. Devolver libro → `PUT /prestamos/:id` (actualizar fechaDevolucion)

### 4. Administración
1. Ver préstamos vencidos → `GET /prestamos/vencidos`
2. Actualizar estados → `PUT /prestamos/actualizar-estado`
3. Gestionar usuarios → Endpoints de usuarios

---

## Notas Importantes

- **IDs de ejemplo**: Los IDs en los ejemplos son referenciales, usar IDs reales de tu base de datos
- **Autenticación**: Algunos endpoints deberían requerir autenticación, pero queda fuera del alcance de este proyecto
- **Fechas**: Usar formato `YYYY-MM-DD` para fechas

## Actualización de Estados
El sistema automáticamente actualiza:
- Estados de préstamos (activo → vencido)
- Contadores de libros disponibles
- Fechas de modificación

---
## Autores:
- Matias Farfan
- Emanuel Gómez Juárez
- Emilia Gómez Juárez
- Marianela Guerrero




