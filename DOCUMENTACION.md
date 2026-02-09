# 📋 Sistema de Gestión de Salón de Belleza - API REST

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen?style=flat-square&logo=spring-boot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?style=flat-square&logo=postgresql)
![License](https://img.shields.io/badge/License-Educational-yellow?style=flat-square)

**API REST para gestión completa de salón de belleza con Arquitectura Hexagonal, JWT + MFA y validaciones de negocio.**

---

## 🎯 Descripción General

Sistema backend desarrollado con **Spring Boot 3.5.7** y **Java 21** que implementa **Arquitectura Hexagonal** (Ports & Adapters) para la gestión integral de un salón de belleza.

### Características Principales

- **Gestión Completa**: CRUD de Clientes, Empleados, Servicios, Recursos, Citas y Usuarios
- **Seguridad Avanzada**: JWT + MFA (TOTP), BCrypt, roles (OWNER/RECEPTIONIST)
- **Validaciones de Negocio**: Disponibilidad de empleados, recursos, horarios laborales
- **Base de Datos**: PostgreSQL 17 con Flyway para migraciones versionadas
- **Documentación**: OpenAPI/Swagger interactiva
- **Testing**: Tests de integración con Testcontainers
- **Dockerizado**: Docker Compose para desarrollo

### Stack Tecnológico

| Categoría | Tecnología |
|-----------|-----------|
| **Framework** | Spring Boot 3.5.7, Spring Security, Spring Data JPA |
| **Base de Datos** | PostgreSQL 17, Flyway |
| **Seguridad** | JWT (jjwt 0.13.0), TOTP (totp 1.7.1), BCrypt |
| **Mapeo** | MapStruct 1.6.3, Lombok |
| **Documentación** | SpringDoc OpenAPI 2.8.14 |
| **Testing** | JUnit 5, Testcontainers |
| **Build** | Gradle 8.x |

**Versión:** 0.0.1-SNAPSHOT | **Última actualización:** Enero 31, 2026

---

## 🏗️ Arquitectura Hexagonal

El proyecto implementa **Arquitectura Hexagonal** (Ports & Adapters) con **Clean Architecture**:

```
┌─────────────────────────────────────────────────────────┐
│  INFRASTRUCTURE (Adapters)                              │
│  • Controllers REST (Web)                               │
│  • JPA Repositories (Persistence)                       │
│  • Security Config (JWT, MFA)                           │
├─────────────────────────────────────────────────────────┤
│  APPLICATION (Use Cases)                                │
│  • Business Logic (Create, Update, Delete, Find, List)  │
│  • DTOs (Request/Response)                              │
│  • Mappers (DTO ↔ Domain)                               │
├─────────────────────────────────────────────────────────┤
│  DOMAIN (Core Business)                                 │
│  • Entities (Cliente, Empleado, Cita, Servicio, etc.)  │
│  • Ports (Repository Interfaces)                        │
│  • Exceptions (Business Rules)                          │
│  • Value Objects (Rol, EstadoCita, FranjaLaboral)      │
└─────────────────────────────────────────────────────────┘
```

### Beneficios de la Arquitectura

1. **Independencia de Frameworks**: La lógica de negocio no depende de Spring Boot
2. **Testeable**: Use Cases se prueban sin infraestructura
3. **Mantenible**: Separación clara de responsabilidades
4. **Flexible**: Fácil cambiar BD, UI o frameworks sin afectar el dominio

---

## 📂 Estructura del Proyecto

```
src/main/java/com/salondebelleza/salondebelleza/
├── SalonDeBellezaApplication.java           # Main class
└── agendamiento/                            # Módulo principal
    ├── domain/                              # 🔵 CAPA DE DOMINIO
    │   ├── entities/                        # Entidades JPA
    │   │   ├── Calificacion.java            # Empleado-Servicio (many-to-many)
    │   │   ├── Cita.java                    # Entidad principal de citas
    │   │   ├── CitaRecursos.java            # Recursos asignados a cita
    │   │   ├── Cliente.java
    │   │   ├── Empleado.java
    │   │   ├── Recurso.java
    │   │   ├── Servicio.java
    │   │   ├── ServicioRecursos.java        # Recursos requeridos por servicio
    │   │   └── Usuario.java                 # Sistema de autenticación
    │   ├── exception/                       # Excepciones de negocio
    │   │   ├── ClienteNoEncontradoException.java
    │   │   ├── SolapamientoException.java   # Validación de horarios
    │   │   └── ...                          # (otras excepciones)
    │   ├── port/                            # Interfaces (Dependency Inversion)
    │   │   ├── CitaRepositoryPort.java
    │   │   ├── ClienteRepositoryPort.java
    │   │   └── ...
    │   └── valueobjects/
    │       ├── EstadoCita.java              # PENDIENTE, CONFIRMADA, CANCELADA
    │       ├── FranjaLaboral.java           # Horarios de trabajo
    │       └── Rol.java                     # OWNER, RECEPTIONIST
    │
    ├── application/                         # 🟢 CAPA DE APLICACIÓN
    │   ├── usecase/                         # Casos de uso (lógica de negocio)
    │   │   ├── auth/
    │   │   │   ├── LoginUseCase.java
    │   │   │   └── VerificarMfaUseCase.java
    │   │   ├── cita/                        # CRUD completo
    │   │   │   ├── CrearCitaUseCase.java    # Validaciones: horarios, recursos
    │   │   │   ├── ActualizarCitaUseCase.java
    │   │   │   ├── BuscarCitaUseCase.java
    │   │   │   ├── EliminarCitaUseCase.java
    │   │   │   └── ListarCitaUseCase.java
    │   │   ├── cliente/                     # Gestión de clientes
    │   │   ├── empleado/                    # Gestión de empleados
    │   │   ├── recurso/                     # Gestión de recursos
    │   │   ├── servicio/                    # Gestión de servicios
    │   │   └── usuario/                     # Gestión de usuarios
    │   ├── dto/                             # Data Transfer Objects
    │   │   ├── auth/
    │   │   │   ├── LoginRequest.java
    │   │   │   ├── LoginResponse.java
    │   │   │   └── MfaVerificationRequest.java
    │   │   ├── cita/
    │   │   ├── cliente/
    │   │   └── ...
    │   └── mapper/                          # MapStruct mappers (DTO ↔ Domain)
    │
    └── infrastructure/                      # 🟡 CAPA DE INFRAESTRUCTURA
        ├── adapter/
        │   ├── web/                         # REST Controllers
        │   │   ├── AuthController.java
        │   │   ├── CitaController.java
        │   │   ├── ClienteController.java
        │   │   ├── EmpleadoController.java
        │   │   ├── RecursoController.java
        │   │   ├── ServicioController.java
        │   │   ├── UsuarioController.java
        │   │   └── exception/               # Global Exception Handler
        │   │       └── GlobalExceptionHandler.java
        │   └── persistence/                 # JPA Adapters
        │       ├── CitaPersistenceAdapter.java
        │       ├── ClientePersistenceAdapter.java
        │       ├── data/                    # JPA Entities (if different from domain)
        │       ├── mapper/                  # Persistence Mappers
        │       └── repository/              # JpaRepository interfaces
        │           ├── CitaRepository.java
        │           ├── ClienteRepository.java
        │           └── ...
        └── config/                          # Configuraciones Spring
            ├── OpenApiConfig.java           # Swagger/OpenAPI
            ├── SecurityConfig.java          # Spring Security
            ├── TotpConfig.java              # MFA Configuration
            └── security/
                ├── ApplicationConfig.java
                ├── JwtAuthenticationFilter.java
                └── JwtService.java

src/main/resources/
├── application.properties                   # Configuración principal
└── db/migration/                            # Flyway migrations
    ├── V1__crear_tabla_clientes.sql
    ├── V2__crear_tabla_recursos.sql
    ├── V3__crear_tabla_clientes_y_serviciorecursos.sql
    ├── V4__crear_tabla_empleados_franjas_laborales_y_calificaciones.sql
    ├── V5__crear_tabla_citas_y_detalle_recursos.sql
    └── V6__seguridad_avanzada.sql

src/test/java/
└── com/salondebelleza/salondebelleza/
    ├── CitaControllerTest.java              # Tests de integración con Testcontainers
    ├── ClienteControllerTest.java
    ├── EmpleadoControllerTest.java
    ├── RecursoControllerTest.java
    └── ServicioControllerTest.java
```

### Descripción de Capas

**🔵 Domain**: Lógica de negocio pura, independiente de frameworks. Define entidades, reglas de negocio y contratos (ports).

**🟢 Application**: Orquesta la lógica de negocio mediante Use Cases. Convierte DTOs en entidades de dominio.

**🟡 Infrastructure**: Implementa detalles técnicos (REST, JPA, Security). Depende de Domain y Application.

---

## 💾 Modelo de Base de Datos

### Entidades Principales

| Tabla | Descripción | Campos Clave |
|-------|-------------|--------------|
| **clientes** | Información de clientes | id, nombre, apellido, email, telefono, fecha_registro |
| **empleados** | Empleados del salón | id, nombre, apellido, email, telefono, activo |
| **servicios** | Servicios ofrecidos | id, nombre, descripcion, precio, duracion_minutos |
| **recursos** | Inventario (productos, herramientas) | id, nombre, tipo, cantidad_disponible, cantidad_minima |
| **citas** | Agendamiento de servicios | id, id_cliente, id_empleado, id_servicio, fecha_hora, estado, observaciones |
| **usuarios** | Autenticación y roles | id, username, password_hash, rol (OWNER/RECEPTIONIST), mfa_enabled, mfa_secret |

### Tablas de Relación (Many-to-Many)

| Tabla | Relación | Propósito |
|-------|----------|-----------|
| **calificaciones** | empleados ↔ servicios | Define qué servicios puede realizar cada empleado |
| **franjas_laborales** | empleados → horarios | Horarios de trabajo por empleado y día de semana |
| **servicio_recursos** | servicios ↔ recursos | Recursos necesarios para cada servicio |
| **cita_recursos** | citas ↔ recursos | Recursos reservados para una cita específica |

### Características Importantes

- **Soft Delete**: Las entidades usan el campo `activo` (boolean) para eliminación lógica
- **Validaciones**: Constraints de UNIQUE, NOT NULL, CHECK implementados en BD
- **Índices**: Creados en campos de búsqueda frecuente (email, nombre, fecha_hora)
- **Relaciones**: FK con ON DELETE CASCADE/RESTRICT según reglas de negocio
- **Auditoría**: Timestamps de creación y actualización en entidades principales

### Diagrama de Relaciones Simplificado

```
┌─────────────┐       ┌──────────────┐       ┌───────────┐
│  clientes   │       │   empleados  │       │ servicios │
│             │       │              │       │           │
│ • id (PK)   │       │ • id (PK)    │       │ • id (PK) │
│ • nombre    │       │ • nombre     │       │ • nombre  │
│ • email     │       │ • activo     │       │ • precio  │
└──────┬──────┘       └────┬─────────┘       └─────┬─────┘
       │                   │                        │
       │                   │                        │
       │    ┌──────────────┴─────────┐             │
       │    │  calificaciones        │             │
       │    │  (empleado-servicio)   │─────────────┘
       │    └────────────────────────┘
       │
       │              ┌─────────────────────┐
       └──────────────┤      citas          │
                      │ • id (PK)           │
                      │ • id_cliente (FK)   │
                      │ • id_empleado (FK)  │
                      │ • id_servicio (FK)  │
                      │ • fecha_hora        │
                      │ • estado            │
                      └──────────┬──────────┘
                                 │
                      ┌──────────┴──────────┐
                      │   cita_recursos     │
                      │   (recursos usados) │
                      └─────────────────────┘
                                 │
                      ┌──────────┴──────────┐
                      │     recursos        │
                      │ • id (PK)           │
                      │ • nombre            │
                      │ • cantidad          │
                      └─────────────────────┘
```

---

## 🔐 Sistema de Seguridad

### Arquitectura de Autenticación

El sistema implementa **JWT (JSON Web Token)** con **MFA (Multi-Factor Authentication)** usando **TOTP** (Time-based One-Time Password).

### Flujo de Autenticación

```
1. Login Inicial
   ├─→ POST /api/v1/auth/login
   │   Body: { "username": "admin", "password": "pass123" }
   │   
   ├─→ Sistema verifica credenciales
   │   
   └─→ Respuestas:
       ├─ MFA Habilitado: Pre-Auth Token (temporal, 5 min)
       └─ MFA Deshabilitado: Access Token completo

2. Verificación MFA (si está habilitado)
   ├─→ POST /api/v1/auth/verify-mfa
   │   Body: { "username": "admin", "totpCode": "123456" }
   │   Header: Authorization: Bearer <pre-auth-token>
   │   
   └─→ Access Token completo (1 hora)

3. Acceso a Endpoints Protegidos
   ├─→ GET/POST/PUT/DELETE /api/v1/{recurso}
   │   Header: Authorization: Bearer <access-token>
   │   
   └─→ JwtAuthenticationFilter valida token
       ├─ Token válido: Procesa request
       └─ Token inválido/expirado: 401 Unauthorized
```

### Tipos de Tokens

| Token | Duración | Permisos | Uso |
|-------|----------|----------|-----|
| **Pre-Auth** | 5 minutos | Solo `/auth/verify-mfa` | Completar MFA |
| **Access** | 1 hora | Todos los endpoints | Operaciones normales |

### Roles y Permisos

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| **OWNER** | Dueño del salón | Acceso total (CRUD usuarios, citas, empleados, servicios) |
| **RECEPTIONIST** | Recepcionista | Gestión de citas, clientes (sin usuarios/empleados) |

### Componentes de Seguridad

1. **JwtService**: Genera y valida tokens JWT
2. **JwtAuthenticationFilter**: Intercepta requests y valida tokens
3. **SecurityConfig**: Configura Spring Security (endpoints públicos/privados)
4. **TotpConfig**: Configura generador TOTP para MFA
5. **PasswordEncoder**: BCrypt para hash de contraseñas

### Configuración (application.properties)

```properties
# JWT Configuration
security.jwt.secret-key=<tu-clave-secreta-256-bits>
security.jwt.expiration-time=3600000  # 1 hora en milisegundos

# Base URL API
api.base-path=/api/v1

# Horarios del salón
salon.hora-inicio-servicio=09:00:00
salon.hora-fin-servicio=19:00:00

# PostgreSQL Database
spring.datasource.url=jdbc:postgresql://localhost:5432/salon_de_belleza_db
spring.datasource.username=salon_admin
spring.datasource.password=salon_password

# JPA & Flyway
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.flyway.enabled=true
```

---

## 🚀 API Endpoints

**Base URL**: `http://localhost:8080/api/v1`

### Autenticación (Público)

| Método | Endpoint | Descripción | Request Body |
|--------|----------|-------------|--------------|
| POST | `/auth/login` | Login inicial | `{ "username", "password" }` |
| POST | `/auth/verify-mfa` | Verificar código TOTP | `{ "username", "totpCode" }` |

### Clientes

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/clientes` | Listar todos | ✅ JWT |
| GET | `/clientes/{id}` | Buscar por ID | ✅ JWT |
| GET | `/clientes/buscar?email={email}` | Buscar por email | ✅ JWT |
| POST | `/clientes` | Crear cliente | ✅ JWT |
| PUT | `/clientes/{id}` | Actualizar | ✅ JWT |
| DELETE | `/clientes/{id}` | Eliminar (soft) | ✅ JWT |

### Empleados

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/empleados` | Listar todos | ✅ JWT |
| GET | `/empleados/{id}` | Buscar por ID | ✅ JWT |
| GET | `/empleados/{id}/disponibilidad?fecha={fecha}` | Ver disponibilidad | ✅ JWT |
| POST | `/empleados` | Crear empleado | ✅ JWT (OWNER) |
| PUT | `/empleados/{id}` | Actualizar | ✅ JWT (OWNER) |
| DELETE | `/empleados/{id}` | Eliminar (soft) | ✅ JWT (OWNER) |

### Servicios

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/servicios` | Listar todos | ✅ JWT |
| GET | `/servicios/{id}` | Buscar por ID | ✅ JWT |
| GET | `/servicios/buscar?nombre={nombre}` | Buscar por nombre | ✅ JWT |
| POST | `/servicios` | Crear servicio | ✅ JWT (OWNER) |
| PUT | `/servicios/{id}` | Actualizar | ✅ JWT (OWNER) |
| DELETE | `/servicios/{id}` | Eliminar | ✅ JWT (OWNER) |

### Recursos

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/recursos` | Listar todos | ✅ JWT |
| GET | `/recursos/{id}` | Buscar por ID | ✅ JWT |
| POST | `/recursos` | Crear recurso | ✅ JWT |
| PUT | `/recursos/{id}` | Actualizar | ✅ JWT |
| DELETE | `/recursos/{id}` | Eliminar | ✅ JWT |

### Citas

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/citas` | Listar todas | ✅ JWT |
| GET | `/citas/{id}` | Buscar por ID | ✅ JWT |
| GET | `/citas/cliente/{idCliente}` | Citas de cliente | ✅ JWT |
| GET | `/citas/empleado/{idEmpleado}` | Citas de empleado | ✅ JWT |
| POST | `/citas` | Crear cita | ✅ JWT |
| PUT | `/citas/{id}` | Actualizar | ✅ JWT |
| DELETE | `/citas/{id}` | Cancelar cita | ✅ JWT |

### Usuarios

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/usuarios` | Listar usuarios | ✅ JWT (OWNER) |
| GET | `/usuarios/{id}` | Buscar por ID | ✅ JWT (OWNER) |
| POST | `/usuarios` | Crear usuario | ✅ JWT (OWNER) |
| PUT | `/usuarios/{id}` | Actualizar | ✅ JWT (OWNER) |
| DELETE | `/usuarios/{id}` | Desactivar usuario | ✅ JWT (OWNER) |

---

## 📝 Ejemplos de Uso

### 1. Login sin MFA

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "mfaRequired": false
}
```

### 2. Login con MFA Habilitado

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "owner", "password": "owner123"}'
```

**Response (200 OK):**
```json
{
  "preAuthToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "mfaRequired": true,
  "message": "Por favor, ingresa tu código TOTP de 6 dígitos"
}
```

**Verificar MFA:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/verify-mfa \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <pre-auth-token>" \
  -d '{"username": "owner", "totpCode": "123456"}'
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "mfaRequired": false
}
```

### 3. Crear Cliente

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/clientes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <access-token>" \
  -d '{
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan.perez@email.com",
    "telefono": "+57 300 123 4567"
  }'
```

**Response (201 Created):**
```json
{
  "id": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan.perez@email.com",
  "telefono": "+57 300 123 4567",
  "fechaRegistro": "2026-01-31T10:30:00"
}
```

### 4. Crear Cita (con validaciones automáticas)

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/citas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <access-token>" \
  -d '{
    "idCliente": 1,
    "idEmpleado": 2,
    "idServicio": 5,
    "fechaHora": "2026-02-15T14:00:00",
    "observaciones": "Cliente prefiere tinte sin amoníaco"
  }'
```

**Validaciones Automáticas:**
- ✅ Empleado está activo
- ✅ Empleado tiene franja laboral en ese día/hora
- ✅ Empleado está calificado para el servicio
- ✅ No hay solapamiento con otras citas del empleado
- ✅ Recursos necesarios están disponibles
- ✅ Horario dentro del rango del salón (09:00-19:00)

**Response (201 Created):**
```json
{
  "id": 10,
  "cliente": {
    "id": 1,
    "nombre": "Juan Pérez"
  },
  "empleado": {
    "id": 2,
    "nombre": "María López"
  },
  "servicio": {
    "id": 5,
    "nombre": "Tinte de Cabello",
    "duracion": 90
  },
  "fechaHora": "2026-02-15T14:00:00",
  "estado": "PENDIENTE",
  "observaciones": "Cliente prefiere tinte sin amoníaco",
  "recursosAsignados": [
    {"recurso": "Tinte Orgánico", "cantidad": 1},
    {"recurso": "Oxidante 20 Vol", "cantidad": 1}
  ]
}
```

---

## 🎨 Patrones de Diseño Implementados

### 1. Arquitectura Hexagonal (Ports & Adapters)

**Propósito**: Aislar la lógica de negocio de detalles técnicos.

**Implementación**:
- **Ports**: Interfaces en `domain/port/` (ej: `ClienteRepositoryPort`)
- **Adapters**: Implementaciones en `infrastructure/adapter/` (ej: `ClientePersistenceAdapter`)

**Beneficio**: Cambiar BD o UI sin tocar la lógica de negocio.

### 2. Use Case Pattern

**Propósito**: Cada caso de uso = 1 clase con responsabilidad única.

**Ejemplo**: `CrearCitaUseCase`, `ActualizarClienteUseCase`

**Beneficio**: Testing fácil, código reutilizable.

### 3. DTO Pattern

**Propósito**: Separar modelos de API de entidades de BD.

**Implementación**:
- **Request DTOs**: `CrearClienteRequest`, `ActualizarCitaRequest`
- **Response DTOs**: `ClienteResponse`, `CitaResponse`

**Beneficio**: Evita exponer estructura interna de BD, permite versionamiento de API.

### 4. Mapper Pattern (MapStruct)

**Propósito**: Conversión automática DTO ↔ Entity.

**Ejemplo**:
```java
@Mapper(componentModel = "spring")
public interface ClienteMapper {
    Cliente toEntity(CrearClienteRequest dto);
    ClienteResponse toResponse(Cliente entity);
}
```

**Beneficio**: Código boilerplate eliminado, type-safe.

### 5. Repository Pattern

**Propósito**: Abstracción de acceso a datos.

**Implementación**: Spring Data JPA con interfaces personalizadas.

### 6. Dependency Injection

**Propósito**: Inversión de control y bajo acoplamiento.

**Implementación**: Constructor injection con `@Autowired`.

---

## 🔧 Configuración y Despliegue

### Requisitos Previos

- **Java 21** (JDK)
- **Docker & Docker Compose** (para BD)
- **Gradle 8.x** (incluido con wrapper)

### Configuración Rápida

#### 1. Clonar Repositorio
```bash
git clone <repo-url>
cd SalonDeBelleza
```

#### 2. Levantar Base de Datos (PostgreSQL)
```bash
docker-compose up -d
```

Esto crea:
- Container `postgres_salon` en puerto `5432`
- Base de datos `salon_de_belleza_db`
- Usuario `salon_admin` / Password `salon_password`

#### 3. Compilar Proyecto
```bash
./gradlew clean build
```

#### 4. Ejecutar Aplicación
```bash
./gradlew bootRun
```

La aplicación inicia en `http://localhost:8080`

### Acceso a Swagger UI

**URL**: `http://localhost:8080/swagger-ui/index.html`

Swagger permite:
- Ver todos los endpoints
- Probar APIs interactivamente
- Ver modelos de Request/Response

### Variables de Entorno (Producción)

Para producción, sobrescribir `application.properties`:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://db-host:5432/salon_db
export SPRING_DATASOURCE_USERNAME=prod_user
export SPRING_DATASOURCE_PASSWORD=secure_password
export SECURITY_JWT_SECRET_KEY=<clave-secreta-256-bits>
export SECURITY_JWT_EXPIRATION_TIME=3600000
```

### Docker Build (Opcional)

```bash
# Construir imagen
docker build -t salon-belleza-api .

# Ejecutar contenedor
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/salon_de_belleza_db \
  salon-belleza-api
```

---

## 🧪 Testing

### Ejecutar Tests

```bash
# Todos los tests
./gradlew test

# Tests de integración con Testcontainers
./gradlew test --tests "*ControllerTest"

# Con reporte HTML
./gradlew test
# Ver en: build/reports/tests/test/index.html
```

### Estrategia de Testing

| Tipo | Herramienta | Cobertura |
|------|-------------|-----------|
| **Integración** | Testcontainers + MockMvc | Controllers con BD real |
| **Unitarios** | JUnit 5 + Mockito | Use Cases (en desarrollo) |

### Ejemplo de Test de Integración

```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class ClienteControllerTest {
    
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>("postgres:17");
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void crearCliente_DeberiaRetornar201() throws Exception {
        mockMvc.perform(post("/api/v1/clientes")
            .contentType(APPLICATION_JSON)
            .content("{\"nombre\":\"Juan\", ...}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }
}
```

---

## 📊 Estado del Proyecto

### ✅ Implementado

- [x] CRUD completo de todas las entidades
- [x] Autenticación JWT + MFA (TOTP)
- [x] Validaciones de negocio en citas
- [x] Soft delete en entidades principales
- [x] Tests de integración con Testcontainers
- [x] Documentación OpenAPI/Swagger
- [x] Migraciones de BD con Flyway
- [x] Docker Compose para desarrollo

### 🚧 En Desarrollo

- [ ] Tests unitarios de Use Cases
- [ ] Paginación en endpoints de listado
- [ ] Filtros avanzados en búsquedas
- [ ] Validación de formato de teléfono internacional

### 📋 Roadmap Futuro

**Corto Plazo:**
- [ ] Sistema de notificaciones (email/SMS)
- [ ] Reportes y estadísticas (citas, ingresos)
- [ ] Frontend React/Vue

**Mediano Plazo:**
- [ ] Sistema de pagos (integración pasarelas)
- [ ] Facturación electrónica
- [ ] App móvil (React Native/Flutter)
- [ ] Cache con Redis

**Largo Plazo:**
- [ ] Multi-tenancy (múltiples sucursales)
- [ ] Sistema de fidelización (puntos)
- [ ] IA para recomendaciones de servicios

---

## ❓ FAQ y Solución de Problemas

### ¿Por qué uso Arquitectura Hexagonal?

**R**: Para separar lógica de negocio de frameworks. Facilita testing y cambios tecnológicos sin reescribir el core.

### ¿Cómo funciona el MFA?

**R**: Genera códigos TOTP de 6 dígitos válidos por 30 segundos usando un secret único por usuario (como Google Authenticator).

### Error: "Table 'clientes' doesn't exist"

**Solución**:
1. Verificar que PostgreSQL esté corriendo: `docker ps`
2. Ejecutar migraciones manualmente: `./gradlew flywayMigrate`
3. Revisar logs de Flyway en consola

### Error: 401 Unauthorized en endpoints

**Causas comunes**:
1. Token expirado (duración: 1 hora)
2. Token inválido (firmado con otra clave)
3. Header incorrecto (debe ser `Authorization: Bearer <token>`)

**Solución**: Hacer login nuevamente y obtener un token fresco.

### ¿Cómo habilitar MFA para un usuario?

**Opción 1 (Manual - BD)**:
```sql
UPDATE usuarios 
SET mfa_enabled = true, 
    mfa_secret = 'SECRET_BASE32_AQUI' 
WHERE username = 'usuario';
```

**Opción 2 (Programático)**:
Implementar endpoint `/usuarios/{id}/enable-mfa` que genere un QR code.

### ¿Cómo agregar un nuevo endpoint?

1. Crear UseCase en `application/usecase/`
2. Crear DTOs en `application/dto/`
3. Agregar método en Controller con anotaciones OpenAPI
4. Agregar tests en `test/`

---

## 📖 Glosario Técnico

| Término | Definición |
|---------|------------|
| **Arquitectura Hexagonal** | Patrón que separa lógica de negocio mediante ports (interfaces) y adapters (implementaciones) |
| **Clean Architecture** | Arquitectura en capas concéntricas con dependencias hacia el dominio |
| **DTO** | Data Transfer Object - Objeto para transferir datos entre capas |
| **JWT** | JSON Web Token - Token autofirmado para autenticación stateless |
| **MFA** | Multi-Factor Authentication - Autenticación en dos pasos |
| **TOTP** | Time-based One-Time Password - Código temporal de 6 dígitos |
| **Soft Delete** | Marcar registro como inactivo sin eliminarlo físicamente (`activo=false`) |
| **Port** | Interfaz que define contrato entre capas (ej: `RepositoryPort`) |
| **Adapter** | Implementación concreta de un port (ej: `PersistenceAdapter`) |
| **Use Case** | Caso de uso que encapsula lógica de negocio específica |
| **Flyway** | Herramienta de versionamiento de BD mediante migraciones SQL |
| **Testcontainers** | Framework para tests de integración con contenedores Docker |
| **MapStruct** | Librería para generar mappers automáticos entre DTOs y Entities |

---

## 📝 Notas Finales

**Proyecto educativo - SENA 2026**  
**Versión de la Documentación:** 4.0 (Optimizada para LLMs)  
**Última actualización:** Enero 31, 2026

### Créditos

Desarrollado como proyecto formativo del SENA utilizando las mejores prácticas de la industria en arquitectura de software, seguridad y testing.

### Recursos Adicionales

- [Documentación Spring Boot](https://spring.io/projects/spring-boot)
- [Arquitectura Hexagonal](https://alistair.cockburn.us/hexagonal-architecture/)
- [JWT.io](https://jwt.io/) - Debug de tokens JWT
- [RFC 6238 - TOTP](https://tools.ietf.org/html/rfc6238)

---

**¿Dudas o sugerencias?** Abre un issue o contacta al equipo de desarrollo.
