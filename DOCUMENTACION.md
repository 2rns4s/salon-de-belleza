# Documentación Técnica - API Salón de Belleza

![Java 21](https://img.shields.io/badge/Java-21-orange) ![Spring Boot 3.5.7](https://img.shields.io/badge/Spring%20Boot-3.5.7-green) ![PostgreSQL 17](https://img.shields.io/badge/PostgreSQL-17-blue)

API REST con Arquitectura Hexagonal: JWT + MFA, validaciones de negocio, CRUD completo.

---

## Arquitectura Hexagonal

```
Infrastructure  → Controllers, JPA Repositories, Security (JWT+MFA)
Application     → Use Cases (28), DTOs, Mappers (12)
Domain          → Entities (10), Ports (6), Exceptions (16), ValueObjects (2)
```

**Flujo:** Controller → UseCase → Port → Adapter → Repository → DB

**Beneficios:** Testeable, independiente de frameworks, fácil de mantener y extender.

---

## Estructura del Proyecto

```
com.salondebelleza.salondebelleza/
├── SalonDeBellezaApplication.java
│
└── agendamiento/
    │
    ├── domain/                                    # CAPA NÚCLEO
    │   ├── entities/                             # Modelos del dominio (10)
    │   │   ├── Calificacion.java
    │   │   ├── Cita.java
    │   │   ├── CitaRecursos.java
    │   │   ├── Cliente.java
    │   │   ├── Empleado.java
    │   │   ├── EstadoCita.java                   # Enum: CONFIRMADA, CANCELADA, REALIZADA
    │   │   ├── Recurso.java
    │   │   ├── Servicio.java
    │   │   ├── ServicioRecursos.java
    │   │   └── Usuario.java
    │   │
    │   ├── port/                                 # Interfaces de repositorio (6)
    │   │   ├── CitaRepositoryPort.java
    │   │   ├── ClienteRepositoryPort.java
    │   │   ├── EmpleadoRepositoryPort.java
    │   │   ├── RecursoRepositoryPort.java
    │   │   ├── ServicioRepositoryPort.java
    │   │   └── UsuarioRepositoryPort.java
    │   │
    │   ├── exception/                            # Excepciones de negocio (16)
    │   │   ├── AccesoDenegadoException.java
    │   │   ├── CitaExistenteException.java
    │   │   ├── CitaFueraDeHorarioException.java
    │   │   ├── CitaNoEncontradaException.java
    │   │   ├── ClienteExistenteException.java
    │   │   ├── ClienteNoEncontradoException.java
    │   │   ├── EmpleadoExistenteException.java
    │   │   ├── EmpleadoNoExistenteException.java
    │   │   ├── EmpleadoNoTrabajaEnEseHorarioException.java
    │   │   ├── RecursoExistenteException.java
    │   │   ├── RecursoNoEncontradoException.java
    │   │   ├── ServicioExistenteException.java
    │   │   ├── ServicioNoEncontradoException.java
    │   │   ├── SolapamientoException.java
    │   │   ├── UsuarioExistenteException.java
    │   │   └── UsuarioNoExistenteException.java
    │   │
    │   └── valueobjects/                         # Objetos de valor (2)
    │       ├── FranjaLaboral.java
    │       └── Rol.java                          # Enum: OWNER, RECEPTIONIST
    │
    ├── application/                               # CAPA DE APLICACIÓN
    │   ├── usecase/                              # Casos de uso (28)
    │   │   ├── auth/                             # Autenticación (2)
    │   │   │   ├── LoginUseCase.java
    │   │   │   └── VerificarMfaUseCase.java
    │   │   ├── cita/                             # Gestión de citas (5)
    │   │   │   ├── ActualizarCitaUseCase.java
    │   │   │   ├── BuscarCitaUseCase.java
    │   │   │   ├── CrearCitaUseCase.java
    │   │   │   ├── EliminarCitaUseCase.java
    │   │   │   └── ListarCitaUseCase.java
    │   │   ├── cliente/                          # Gestión de clientes (5)
    │   │   │   ├── ActualizarClienteUseCase.java
    │   │   │   ├── BuscarClienteUseCase.java
    │   │   │   ├── CrearClienteUseCase.java
    │   │   │   ├── EliminarClienteUseCase.java
    │   │   │   └── ListarClienteUseCase.java
    │   │   ├── disponibilidad/                   # Consulta de disponibilidad (1)
    │   │   │   └── ObtenerDisponibilidadUseCase.java
    │   │   ├── empleado/                         # Gestión de empleados (5)
    │   │   │   ├── ActualizarEmpleadoUseCase.java
    │   │   │   ├── BuscarEmpleadoUseCase.java
    │   │   │   ├── CrearEmpleadoUseCase.java
    │   │   │   ├── EliminarEmpleadoUseCase.java
    │   │   │   └── ListarEmpleadoUseCase.java
    │   │   ├── recurso/                          # Gestión de recursos (5)
    │   │   │   ├── ActualizarRecursoUseCase.java
    │   │   │   ├── BuscarRecursoUseCase.java
    │   │   │   ├── CrearRecursoUseCase.java
    │   │   │   ├── EliminarRecursoUseCase.java
    │   │   │   └── ListarRecursoUseCase.java
    │   │   ├── servicio/                         # Gestión de servicios (5)
    │   │   │   ├── ActualizarServicioUseCase.java
    │   │   │   ├── BuscarServicioUseCase.java
    │   │   │   ├── CrearServicioUseCase.java
    │   │   │   ├── EliminarServicioUseCase.java
    │   │   │   └── ListarServicioUseCase.java
    │   │   └── usuario/                          # Gestión de usuarios (5)
    │   │       ├── ActualizarUsuarioUseCase.java
    │   │       ├── BuscarUsuarioUseCase.java
    │   │       ├── CrearUsuarioUseCase.java
    │   │       ├── EliminarUsuarioUseCase.java
    │   │       └── ListarUsuarioUseCase.java
    │   │
    │   ├── dto/                                  # Request/Response DTOs
    │   └── mapper/                               # Mappers DTO ↔ Entity (6)
    │       ├── CitaDTOMapper.java
    │       ├── ClienteDTOMapper.java
    │       ├── EmpleadoDTOMapper.java
    │       ├── RecursoDTOMapper.java
    │       ├── ServicioDTOMapper.java
    │       └── UsuarioDTOMapper.java
    │
    └── infrastructure/                            # CAPA DE INFRAESTRUCTURA
        ├── adapter/
        │   ├── web/                              # Controllers (8)
        │   │   ├── AuthController.java
        │   │   ├── CitaController.java
        │   │   ├── ClienteController.java
        │   │   ├── DisponibilidadController.java
        │   │   ├── EmpleadoController.java
        │   │   ├── RecursoController.java
        │   │   ├── ServicioController.java
        │   │   ├── UsuarioController.java
        │   │   └── exception/
        │   │       └── GlobalExceptionHandler.java
        │   │
        │   └── persistence/                      # Adaptadores de persistencia (6)
        │       ├── CitaPersistenceAdapter.java
        │       ├── ClientePersistenceAdapter.java
        │       ├── EmpleadoPersistenceAdapter.java
        │       ├── RecursoPersistenceAdapter.java
        │       ├── ServicioPersistenceAdapter.java
        │       ├── UsuarioPersistenceAdapter.java
        │       ├── data/                         # Entidades JPA
        │       ├── mapper/                       # Mappers Entity ↔ Data
        │       └── repository/                   # Spring Data JPA Repositories
        │
        └── config/                               # Configuración (4 + 3 en security)
            ├── OpenApiConfig.java
            ├── SecurityConfig.java
            ├── TotpConfig.java
            └── security/
                ├── ApplicationConfig.java
                ├── JwtAuthenticationFilter.java
                └── JwtService.java
```

**Domain:** Lógica de negocio pura, sin dependencias externas  
**Application:** Orquesta casos de uso, conecta Domain con Infrastructure  
**Infrastructure:** Implementa Ports, maneja frameworks (Spring, JPA, Security)

---

## Base de Datos (PostgreSQL 17)

### Migraciones Flyway

**Ubicación:** `src/main/resources/db/migration/`  
**Ejecución:** Automática al iniciar la aplicación

| Versión | Archivo | Tablas Creadas | Descripción |
|---------|---------|----------------|-------------|
| **V1** | `crear_tabla_clientes.sql` | `clientes` | Registros de clientes con documento único |
| **V2** | `crear_tabla_recursos.sql` | `recursos` | Inventario de recursos (tijeras, secadores, etc.) |
| **V3** | `crear_tabla_clientes_y_serviciorecursos.sql` | `servicios`, `servicio_recursos` | Servicios ofrecidos + relación M:N con recursos |
| **V4** | `crear_tabla_empleados_franjas_laborales_y_calificaciones.sql` | `empleados`, `franjas_laborales`, `calificaciones` | Personal, horarios de trabajo, servicios que puede realizar |
| **V5** | `crear_tabla_citas_y_detalle_recursos.sql` | `citas`, `citas_recursos` | Agendamientos + recursos asignados + índices de optimización |
| **V6** | `seguridad_avanzada.sql` | `usuarios` | Autenticación JWT + MFA (TOTP) |

**Orden de ejecución:** V1 → V2 → V3 → V4 → V5 → V6 (respeta dependencias FK)

### Entidades del Dominio

| Entidad | Atributos (tipo) | Relaciones |
|---------|------------------|------------|
| **Cliente** | `idCliente: Long`<br>`nombre: String`<br>`apellido: String`<br>`documento: String` (único)<br>`telefono: String`<br>`citas: List<Cita>` | 1:N con Cita |
| **Empleado** | `idEmpleado: Long`<br>`nombre: String`<br>`apellido: String`<br>`correo: String` (único)<br>`telefono: String`<br>`calificaciones: List<Calificacion>`<br>`franjasLaborales: List<FranjaLaboral>`<br>`citas: List<Cita>` | 1:N con Cita<br>1:N con FranjaLaboral<br>1:N con Calificacion<br>Soft delete |
| **Servicio** | `idServicio: Long`<br>`nombre: String` (único)<br>`duracion: int` (minutos)<br>`calificaciones: List<Calificacion>`<br>`servicioRecursos: List<ServicioRecursos>`<br>`citas: List<Cita>` | 1:N con Cita<br>M:N con Recurso (via ServicioRecursos)<br>N:M con Empleado (via Calificacion) |
| **Recurso** | `idRecurso: Long`<br>`nombre: String`<br>`cantidadTotal: int`<br>`serviciosRecursos: List<ServicioRecursos>`<br>`citaRecursos: List<CitaRecursos>` | M:N con Servicio (via ServicioRecursos)<br>M:N con Cita (via CitaRecursos) |
| **Cita** | `idCita: Long`<br>`idCliente: Long`<br>`idEmpleado: Long`<br>`idServicio: Long`<br>`fechaInicio: LocalDateTime`<br>`fechaFin: LocalDateTime`<br>`estado: EstadoCita` (CONFIRMADA, CANCELADA, REALIZADA)<br>`citaRecursos: List<CitaRecursos>` | N:1 con Cliente<br>N:1 con Empleado<br>N:1 con Servicio<br>M:N con Recurso (via CitaRecursos) |
| **Usuario** | `idUsuario: Long`<br>`nombreUsuario: String` (único)<br>`contrasena: String` (hash)<br>`rol: String` (OWNER, RECEPTIONIST)<br>`idEmpleado: Long` (nullable)<br>`mfaEnable: Boolean`<br>`mfaSecret: String`<br>`codigosRecuperacion: String`<br>`activo: Boolean` | N:1 con Empleado (opcional)<br>Soft delete |
| **Calificacion** | `idCalificacion: Long`<br>`idEmpleado: Long`<br>`idServicio: Long` | N:1 con Empleado<br>N:1 con Servicio<br>(Define servicios que un empleado puede realizar) |
| **FranjaLaboral** | `idFranjaLaboral: Long`<br>`idEmpleado: Long`<br>`dia: DayOfWeek` (MONDAY-SUNDAY)<br>`horaInicio: LocalTime`<br>`horaFin: LocalTime` | N:1 con Empleado<br>(Value Object - Define horarios de trabajo) |
| **ServicioRecursos** | `idServicioRecursos: Long`<br>`idServicio: Long`<br>`idRecurso: Long`<br>`nombreRecurso: String`<br>`cantidadRequerida: int` | N:1 con Servicio<br>N:1 con Recurso<br>(Tabla intermedia - recursos necesarios por servicio) |
| **CitaRecursos** | `idCitaRecurso: Long`<br>`idCita: Long`<br>`idRecurso: Long` | N:1 con Cita<br>N:1 con Recurso<br>(Tabla intermedia - recursos asignados a cita) |

### Diagrama de Relaciones

```
Cliente 1──N Cita N──1 Empleado
                 │
                 ├──N──1 Servicio
                 └──N CitaRecursos N──1 Recurso
                 
Empleado 1──N FranjaLaboral
         1──N Calificacion N──1 Servicio

Servicio 1──N ServicioRecursos N──1 Recurso

Usuario N──1 Empleado (opcional, nullable)
```

**Notas técnicas:**
- **Soft Delete:** Empleados y usuarios usan `activo=false` (no se eliminan físicamente de BD)
- **Cascade:** Al eliminar una cita se liberan automáticamente sus `citas_recursos` asociados
- **Índices:** Optimizan búsquedas por `(id_empleado, fecha_inicio)`, `(id_cliente)`, `(id_recurso, fecha_inicio)`
- **Estados de Cita:** Solo CONFIRMADA, CANCELADA, REALIZADA (sin PENDIENTE)
- **Unicidad:** `documento` (Cliente), `correo` (Empleado), `nombreUsuario` (Usuario), `nombre` (Servicio)

---

## Casos de Uso (Application Layer)

Cada entidad tiene sus operaciones CRUD + métodos especializados. Patrón: 1 UseCase = 1 operación.

### Cliente (5 UseCases)

| UseCase | Método | Descripción | Validaciones |
|---------|--------|-------------|--------------|
| `CrearClienteUseCase` | `execute(CrearClienteRequest)` | Registra nuevo cliente | Documento único |
| `ActualizarClienteUseCase` | `execute(ActualizarClienteRequest)` | Modifica datos del cliente | Cliente existe |
| `BuscarClienteUseCase` | `executeById(Long)` | Busca por ID | Cliente existe |
|  | `executeByDocumento(String)` | Busca por documento único | - |
|  | `executeByTelefono(String)` | Busca por teléfono | - |
| `ListarClienteUseCase` | `execute()` | Lista todos los clientes | - |
| `EliminarClienteUseCase` | `execute(Long)` | Elimina cliente (física) | Cliente existe |

### Empleado (5 UseCases)

| UseCase | Método | Descripción | Validaciones |
|---------|--------|-------------|--------------|
| `CrearEmpleadoUseCase` | `execute(CrearEmpleadoRequest)` | Crea empleado con franjas y calificaciones | Correo único |
| `ActualizarEmpleadoUseCase` | `execute(ActualizarEmpleadoRequest)` | Actualiza datos, franjas y calificaciones | Empleado existe |
| `BuscarEmpleadoUseCase` | `executeById(Long)` | Busca por ID | Empleado existe |
|  | `executeByCorreo(String)` | Busca por correo único | - |
| `ListarEmpleadoUseCase` | `execute()` | Lista todos los empleados | - |
| `EliminarEmpleadoUseCase` | `execute(Long)` | Soft delete (`activo=false`) | Empleado existe |

### Servicio (5 UseCases)

| UseCase | Método | Descripción | Validaciones |
|---------|--------|-------------|--------------|
| `CrearServicioUseCase` | `execute(CrearServicioRequest)` | Crea servicio con recursos requeridos | Nombre único |
| `ActualizarServicioUseCase` | `execute(ActualizarServicioRequest)` | Actualiza servicio y recursos | Servicio existe |
| `BuscarServicioUseCase` | `executeById(Long)` | Busca por ID | Servicio existe |
|  | `executeByNombre(String)` | Busca por nombre | - |
| `ListarServicioUseCase` | `execute()` | Lista todos los servicios | - |
| `EliminarServicioUseCase` | `execute(Long)` | Elimina servicio (física) | Servicio existe |

### Recurso (5 UseCases)

| UseCase | Método | Descripción | Validaciones |
|---------|--------|-------------|--------------|
| `CrearRecursoUseCase` | `execute(CrearRecursoRequest)` | Registra recurso con cantidad | - |
| `ActualizarRecursoUseCase` | `execute(ActualizarRecursoRequest)` | Modifica nombre o cantidad total | Recurso existe |
| `BuscarRecursoUseCase` | `executeById(Long)` | Busca por ID | Recurso existe |
|  | `executeByNombre(String)` | Busca por nombre | - |
| `ListarRecursoUseCase` | `execute()` | Lista todos los recursos | - |
| `EliminarRecursoUseCase` | `execute(Long)` | Elimina recurso (física) | Recurso existe |

### Cita (5 UseCases)

| UseCase | Método | Descripción | Validaciones |
|---------|--------|-------------|--------------|
| `CrearCitaUseCase` | `execute(CrearCitaRequest)` | Crea cita con validaciones complejas* | Empleado activo y calificado, franja laboral válida, sin solapamiento, recursos disponibles |
| `ActualizarCitaUseCase` | `execute(ActualizarCitaRequest)` | Cambia fecha/hora o estado | Cita existe |
| `BuscarCitaUseCase` | `executeById(Long)` | Busca por ID | Cita existe |
| `ListarCitaUseCase` | `execute()` | Lista todas las citas | - |
| `EliminarCitaUseCase` | `execute(Long)` | Cancela cita y libera recursos | Cita existe |

**Validaciones en CrearCitaUseCase:* Empleado activo, calificado para el servicio, dentro de franja laboral, sin solapamiento con otras citas, recursos disponibles en cantidad suficiente

### Usuario (5 UseCases)

| UseCase | Método | Descripción | Validaciones |
|---------|--------|-------------|--------------|
| `CrearUsuarioUseCase` | `execute(CrearUsuarioRequest)` | Crea usuario (OWNER/RECEPTIONIST) | Nombre único, contraseña encriptada |
| `ActualizarUsuarioUseCase` | `execute(ActualizarUsuarioRequest)` | Cambia password, rol o activo | Usuario existe |
| `BuscarUsuarioUseCase` | `executeById(Long)` | Busca por ID | Usuario existe |
|  | `executeByNombreUsuario(String)` | Busca por nombre de usuario | - |
| `ListarUsuarioUseCase` | `execute()` | Lista todos los usuarios | - |
| `EliminarUsuarioUseCase` | `execute(Long)` | Soft delete (`activo=false`) | Usuario existe |

### Autenticación (2 UseCases)

| UseCase | Método | Descripción | Validaciones |
|---------|--------|-------------|--------------|
| `LoginUseCase` | `execute(LoginRequest)` | Login con JWT (o pre-auth si MFA) | Credenciales válidas |
| `VerificarMfaUseCase` | `execute(MfaVerificationRequest)` | Valida código TOTP → Token final | Código válido (30s ventana) |

### Disponibilidad (1 UseCase)

| UseCase | Método | Descripción | Validaciones |
|---------|--------|-------------|--------------|
| `ObtenerDisponibilidadUseCase` | `execute(DisponibilidadRequest)` | Calcula horarios libres considerando: franjas laborales, citas existentes, recursos disponibles (intervalos de 30 min) | Empleado calificado para el servicio |

**Total:** 28 UseCases organizados en 8 módulos

---

### Flujo de Autenticación

1. **Login** → `POST /auth/login` → Retorna token (o pre-auth si MFA)
2. **MFA (opcional)** → `POST /auth/verify-mfa` + código TOTP → Token final
3. **Acceso** → Header `Authorization: Bearer {token}` → JwtAuthenticationFilter valida

### Tokens

- **Pre-Auth:** 5 min, solo para `/auth/verify-mfa`
- **Access:** 1 hora, todos los endpoints

### Roles

- **OWNER:** Acceso total, MFA recomendado
- **RECEPTIONIST:** Gestión de citas/clientes (sin usuarios/empleados)

### Componentes

- `JwtService`: Genera/valida tokens
- `JwtAuthenticationFilter`: Intercepta requests
- `SecurityConfig`: Spring Security
- `TotpConfig`: MFA con TOTP
- `GlobalExceptionHandler`: Manejo de errores 401/403

---

## API Endpoints (Base: `/api/v1`)

Ver detalles completos en [Swagger](http://localhost:8080/swagger-ui.html) o [MANUAL_FRONTEND.md](MANUAL_FRONTEND.md)

| Recurso | Endpoints | Auth | Operaciones Disponibles |
|---------|-----------|------|-------------------------|
| `/auth` | `POST /login`, `POST /verify-mfa` | ❌ | Login, verificación MFA |
| `/clientes` | GET, POST, PUT, DELETE | ✅ | CRUD, buscar por ID/documento/teléfono/nombre (GET /, GET /{id}, GET /buscar) |
| `/empleados` | GET, POST, PUT, DELETE | ✅ | CRUD, buscar por ID/correo (GET /, GET /{id}) |
| `/servicios` | GET, POST, PUT, DELETE | ✅ | CRUD, buscar por ID/nombre (GET /, GET /{id}, GET /buscar) |
| `/recursos` | GET, POST, PUT, DELETE | ✅ | CRUD, buscar por ID/nombre (GET /, GET /{id}, GET /buscar) |
| `/citas` | GET, POST, PUT, DELETE | ✅ | CRUD (GET /, GET /{idCita}, POST /, PUT /{idCita}, DELETE /{idCita}) |
| `/usuarios` | GET, POST, PUT, DELETE | ✅ OWNER | CRUD (GET /, GET /{id}, POST /, PUT /{id}, DELETE /{id}) |
| `/disponibilidad` | GET | ✅ | Consulta horarios libres (query params: fecha, idServicio, idEmpleado opcional) |

### Validaciones Automáticas al Crear Citas

1. ✅ Empleado activo y calificado para el servicio
2. ✅ Franja laboral disponible (día y horario)
3. ✅ Sin solapamiento con otras citas del empleado
4. ✅ Recursos disponibles en cantidad suficiente
5. ✅ Fecha futura válida
6. ✅ Horario dentro del rango del salón

---

## Patrones de Diseño

1. **Hexagonal Architecture:** Ports (interfaces) + Adapters (implementaciones)
2. **Use Case Pattern:** 1 clase = 1 operación de negocio
3. **DTO Pattern:** Request/Response separados de entidades
4. **Mapper Pattern:** MapStruct para conversión automática
5. **Repository Pattern:** Abstracción de acceso a datos (Spring Data JPA)
6. **Dependency Injection:** Constructor injection

## Configuración

### Requisitos

- Java 21, Docker, Gradle 8.x

### Inicio Rápido

```bash
docker-compose up -d    # PostgreSQL (puerto 5432)
./gradlew bootRun       # Aplicación (puerto 8080)
```

**Swagger:** http://localhost:8080/swagger-ui.html

### Variables de Entorno (Producción)

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/db
export SPRING_DATASOURCE_USERNAME=user
export SPRING_DATASOURCE_PASSWORD=pass
export SECURITY_JWT_SECRET_KEY=<256-bit-secret>
```

## Testing

```bash
./gradlew test                          # Todos los tests
./gradlew test --tests "*ControllerTest" # Solo integración
```

**Estrategia:** Testcontainers (integración con BD real), JUnit 5

## FAQ

**¿Por qué Hexagonal?** Separa lógica de negocio de frameworks, facilita testing y cambios.

**¿Cómo funciona MFA?** TOTP (códigos 6 dígitos, válidos 30s) como Google Authenticator.

**Error 401?** Token expirado (1h) o inválido → Re-login

**¿Cómo habilitar MFA?** BD: `UPDATE usuarios SET mfa_enable=true WHERE ...`

## Glosario

- **Port:** Interfaz entre capas
- **Adapter:** Implementación de un Port
- **UseCase:** 1 clase = 1 operación de negocio
- **DTO:** Request/Response de API
- **Soft Delete:** `activo=false` en vez de DELETE

---

**SENA 2026** • v0.0.1-SNAPSHOT • Proyecto educativo
