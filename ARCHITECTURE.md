# Arquitectura del Sistema - Salón de Belleza API

![Java 21](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk) ![Spring Boot 3.5.7](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen?style=flat-square&logo=spring-boot) ![PostgreSQL 17](https://img.shields.io/badge/PostgreSQL-17-blue?style=flat-square&logo=postgresql) ![Hexagonal](https://img.shields.io/badge/Architecture-Hexagonal-purple?style=flat-square)

**Última actualización:** Octubre 2026 | **Versión:** 0.0.1-SNAPSHOT

---

## 1. Visión General de Arquitectura Hexagonal

La aplicación implementa **Arquitectura Hexagonal** (también llamada Puertos y Adaptadores) que separa la lógica de negocio de las dependencias externas (frameworks, base de datos, librerías).

### Principios Fundamentales

```
┌─────────────────────────────────────────────────────┐
│           PRESENTACIÓN (HTTP/REST)                  │
│         Controllers (Adaptadores Web)               │
└────────────────┬──────────────────────────────────┘
                 │
┌────────────────▼──────────────────────────────────┐
│         APPLICATION LAYER (Casos de Uso)          │
│  ┌──────────────────────────────────────────┐    │
│  │  UseCases (Orquestación del negocio)     │    │
│  │  DTOs (Contrato de entrada/salida)       │    │
│  │  Mappers (Conversión de datos)           │    │
│  └──────────────────────────────────────────┘    │
└────────────────┬──────────────────────────────────┘
                 │
┌────────────────▼──────────────────────────────────┐
│           DOMAIN LAYER (Lógica Pura)              │
│  ┌──────────────────────────────────────────┐    │
│  │  Entities (Modelos del dominio)          │    │
│  │  Ports (Interfaces/Contratos)            │    │
│  │  Exceptions (Errores de negocio)         │    │
│  │  ValueObjects (Objetos inmutables)       │    │
│  └──────────────────────────────────────────┘    │
└────────────────┬──────────────────────────────────┘
                 │
┌────────────────▼──────────────────────────────────┐
│      INFRASTRUCTURE LAYER (Implementación)        │
│  ┌──────────────────────────────────────────┐    │
│  │  Adapters (Implementación de Ports)      │    │
│  │  Repositories (Persistencia JPA)         │    │
│  │  Config (Spring Security, Swagger, etc.) │    │
│  │  External APIs (Integración externa)     │    │
│  └──────────────────────────────────────────┘    │
└────────────────┬──────────────────────────────────┘
                 │
         ┌───────▼───────┐
         │  PostgreSQL   │
         │  Redis (Cache)│
         └───────────────┘
```

### Flujo de Datos

```
REQUEST
   ↓
Controller → UseCase → Port ↔ Adapter → Repository → Database
             (AppLayer) (Domain) (Infrastructure)
   ↑
RESPONSE
```

**Ventajas de este patrón:**
- ✅ **Testeable:** Lógica de negocio sin dependencias externas
- ✅ **Independiente de Frameworks:** Cambiar Spring, JPA, BD sin afectar dominio
- ✅ **Mantenible:** Cada capa tiene una responsabilidad clara
- ✅ **Escalable:** Fácil agregar nuevas funcionalidades
- ✅ **Desacoplado:** Inversión de dependencias hacia el dominio

---

## 2. Capas de la Aplicación

### 2.1 Domain Layer (Núcleo del Negocio)

La capa más importante: contiene la lógica de negocio pura, SIN dependencias de Spring o frameworks.

#### Entidades de Dominio (10 clases)

| Entidad | Responsabilidad | Características |
|---------|-----------------|-----------------|
| **Cliente** | Registra clientes del salón | Documento único, teléfono, citas |
| **Empleado** | Personal del salón | Correo único, franjas laborales, calificaciones, soft delete |
| **Servicio** | Servicios ofrecidos | Nombre único, duración en minutos, recursos requeridos |
| **Recurso** | Inventario del salón | Cantidad total disponible, asignaciones por cita |
| **Cita** | Agendamiento de servicios | Estados (CONFIRMADA, CANCELADA, REALIZADA), validaciones complejas |
| **Usuario** | Acceso a la aplicación | JWT + MFA (TOTP), roles (OWNER, RECEPTIONIST), soft delete |
| **Calificacion** | Capacidades del empleado | Qué servicios puede realizar cada empleado |
| **FranjaLaboral** | Horarios de trabajo | Día de semana, hora inicio/fin |
| **ServicioRecursos** | Relación M:N | Recursos requeridos por servicio |
| **CitaRecursos** | Relación M:N | Recursos asignados a cada cita |

#### Puertos (Interfaces - 7 puertos)

Definen contratos que DEBEN implementar los adaptadores:

```java
// Ejemplo: ClienteRepositoryPort
public interface ClienteRepositoryPort {
    Cliente buscarPorId(Long id);
    Cliente buscarPorDocumento(String documento);
    List<Cliente> listar();
    Cliente guardar(Cliente cliente);
    void eliminar(Long id);
}
```

**Puertos en el sistema:**
- `CitaRepositoryPort` (Persistencia de citas)
- `ClienteRepositoryPort` (Persistencia de clientes)
- `EmpleadoRepositoryPort` (Persistencia de empleados)
- `RecursoRepositoryPort` (Persistencia de recursos)
- `ServicioRepositoryPort` (Persistencia de servicios)
- `UsuarioRepositoryPort` (Persistencia de usuarios)
- `NotificacionEventPort` (Puerto para publicación de eventos de notificación)

#### Excepciones de Negocio (17 excepciones)

Representan errores de lógica de negocio (NO técnicos):

```
✓ ClienteNoEncontradoException (404)
✓ ClienteNoExistenteException (404)
✓ ClienteExistenteException (409)
✓ EmpleadoNoExistenteException (404)
✓ EmpleadoExistenteException (409)
✓ ServicioNoEncontradoException (404)
✓ ServicioExistenteException (409)
✓ RecursoNoEncontradoException (404)
✓ RecursoExistenteException (409)
✓ CitaNoEncontradaException (404)
✓ CitaExistenteException (409)
✓ SolapamientoException (409)
✓ CitaFueraDeHorarioException (400)
✓ EmpleadoNoTrabajaEnEseHorarioException (400)
✓ UsuarioNoExistenteException (404)
✓ UsuarioExistenteException (409)
✓ AccesoDenegadoException (403)
```

#### Value Objects (2 clases)

Objetos inmutables que representan conceptos del dominio:

- **`FranjaLaboral`:** Representa un horario de trabajo
  - `dia: DayOfWeek` (MONDAY-SUNDAY)
  - `horaInicio: LocalTime`
  - `horaFin: LocalTime`

- **`Rol`:** Enum con roles de usuario
  - `OWNER` (acceso total)
  - `RECEPTIONIST` (gestión de citas/clientes)

**Ubicación:** `src/main/java/com/.../agendamiento/domain/`

---

### 2.2 Application Layer (Casos de Uso)

Orquesta la lógica de negocio, implementa validaciones y coordina el flujo.

#### Casos de Uso (36 UseCases)

1 UseCase = 1 operación de negocio. Patrón:

```java
@Service
@RequiredArgsConstructor
public class CrearClienteUseCase {
    private final ClienteRepositoryPort repository;
    private final ClienteMapper mapper;

    public ClienteResponse execute(CrearClienteRequest request) {
        // 1. Validar entrada
        // 2. Mapear a Entidad
        // 3. Lógica de negocio
        // 4. Persistir (via Port)
        // 5. Mapear a Response
        return mapper.toResponse(savedEntity);
    }
}
```

#### Distribución por módulo

| Módulo | UseCases | Descripción |
|--------|----------|-------------|
| **auth** | 5 | LoginUseCase, VerificarMfaUseCase, GenerarMfaSetupUseCase, HabilitarMfaUseCase, RegistrarUsuarioEmpleadoUseCase |
| **cliente** | 5 | CRUD + búsqueda |
| **empleado** | 5 | CRUD + búsqueda |
| **servicio** | 5 | CRUD + búsqueda |
| **recurso** | 5 | CRUD + búsqueda |
| **cita** | 5 | CRUD (con validaciones complejas) |
| **usuario** | 5 | CRUD (solo OWNER) |
| **disponibilidad** | 1 | Consulta horarios libres |
| **Total** | **36** | - |

#### Data Transfer Objects (DTOs)

Separan el contrato de API de las entidades internas:

```
Request DTOs (CrearClienteRequest, ActualizarClienteRequest, etc.)
Response DTOs (ClienteResponse, EmpleadoResponse, etc.)
```

**Ventajas:**
- Validación en entrada
- Ocultamiento de campos internos
- Versionado de API independiente

#### Eventos de Aplicación (Events)

Representan sucesos relevantes en el ciclo de vida del negocio:

- **`CitaConfirmadaEvent`:** Record inmutable disparado cuando una cita es agendada exitosamente:
  - `idCita: Long`
  - `telefonoCliente: String`
  - `nombreCliente: String`
  - `nombreServicio: String`
  - `fechaHoraCita: String`

#### Mappers (6 mappers)

Conversión automática usando MapStruct:

```
Entity ↔ DTO ↔ JPA Data
```

Mappers:
- `CitaDTOMapper` / `CitaPersistenceMapper`
- `ClienteDTOMapper` / `ClientePersistenceMapper`
- `EmpleadoDTOMapper` / `EmpleadoPersistenceMapper`
- (y 3 más para Servicio, Recurso, Usuario)

**Ubicación:** `src/main/java/com/.../agendamiento/application/`

---

### 2.3 Infrastructure Layer (Adaptadores)

Implementa los Ports y configura frameworks.

#### Adaptadores Web (9 Controllers)

Exponen endpoints REST y validan HTTP:

| Controller | Path | Responsabilidad |
|-----------|------|-----------------|
| `AuthController` | `/auth` | Login, verificación MFA, registro de empleado (`/employee-signup`) |
| `MfaController` | `/usuarios/me/mfa` | Setup de MFA (`/setup`) y habilitación (`/enable`) |
| `ClienteController` | `/clientes` | CRUD clientes |
| `EmpleadoController` | `/empleados` | CRUD empleados |
| `ServicioController` | `/servicios` | CRUD servicios |
| `RecursoController` | `/recursos` | CRUD recursos |
| `CitaController` | `/citas` | CRUD citas (validaciones) |
| `UsuarioController` | `/usuarios` | CRUD usuarios (OWNER only) |
| `DisponibilidadController` | `/disponibilidad` | Consulta horarios |

**Anotaciones relevantes:**
- `@RestController` - Retorna JSON
- `@RequestMapping("${api.base-path}/...")` - Path base configurable
- `@Operation`, `@ApiResponse` - Documentación Swagger
- `@Valid` - Validación de entrada

#### Adaptadores de Persistencia (6 Adapters)

Implementan los Ports y usan Spring Data JPA:

```java
@Repository
@RequiredArgsConstructor
public class ClientePersistenceAdapter implements ClienteRepositoryPort {
    private final ClienteSpringDataRepository repository;
    private final ClientePersistenceMapper mapper;

    @Override
    public Cliente buscarPorId(Long id) {
        return repository.findById(id)
            .map(mapper::toDomain)
            .orElseThrow(() -> new ClienteNoEncontradoException(id));
    }
}
```

**Adaptadores:**
- `CitaPersistenceAdapter`
- `ClientePersistenceAdapter`
- `EmpleadoPersistenceAdapter`
- `RecursoPersistenceAdapter`
- `ServicioPersistenceAdapter`
- `UsuarioPersistenceAdapter`

#### Repositorios Spring Data JPA

Interfaces que heredan de `JpaRepository`:

```
ClienteRepository extends JpaRepository<ClienteData, Long>
// Métodos custom: findByDocumento, findByTelefono, etc.
```

#### Configuración (7 clases)

**Seguridad:**
- `SecurityConfig.java` - Filtros, autorización
- `JwtAuthenticationFilter.java` - Intercepta requests
- `JwtService.java` - Genera/valida tokens JWT
- `ApplicationConfig.java` - Beans de Spring

**Integración y Async:**
- `OpenApiConfig.java` - Swagger/OpenAPI
- `TotpConfig.java` - MFA con TOTP
- `RedisConfig.java` - Cache distribuido
- `AsyncConfig.java` - Soporte asíncrono (@EnableAsync)

#### Adaptadores de Eventos y Notificaciones

- **`SpringEventPublisherAdapter.java`:** Implementa `NotificacionEventPort` publicando eventos mediante `ApplicationEventPublisher`.
- **`TwilioSmsNotificationListener.java`:** Listener asíncrono (`@Async`, `@TransactionalEventListener(phase = AFTER_COMMIT)`) que escucha `CitaConfirmadaEvent` y envía un SMS al cliente con la confirmación mediante Twilio (+57).

#### Conversores de Cifrado (Persistence Converters)

- **`CryptoConverter.java`:** Implementa `AttributeConverter<String, String>` con cifrado simétrico AES y codificación Base64 para columnas sensibles (`documento`, `telefono`).

#### Manejo de Excepciones

`GlobalExceptionHandler` (Advice):

```
Excepción de Negocio → HTTP Status
ClienteNoEncontradoException → 404 NOT_FOUND
ClienteExistenteException → 409 CONFLICT
CitaFueraDeHorarioException → 400 BAD_REQUEST
MethodArgumentNotValidException → 400 (con validaciones)
Exception (genérica) → 500 INTERNAL_SERVER_ERROR
```

**Ubicación:** `src/main/java/com/.../agendamiento/infrastructure/`

---

## 3. Patrones de Diseño Empleados

| Patrón | Implementación | Beneficio |
|--------|----------------|-----------|
| **Hexagonal** | Capas (Domain, Application, Infrastructure) | Desacoplamiento, testabilidad |
| **Repository** | Ports + Adapters JPA | Abstracción de persistencia |
| **UseCase** | 1 Clase = 1 Operación | Responsabilidad única |
| **DTO** | Request/Response DTOs | Contrato de API independiente |
| **Mapper** | MapStruct automático | Conversión sin boilerplate |
| **Dependency Injection** | Constructor injection | Inyección clara de dependencias |
| **Strategy** | Validaciones en UseCases | Lógica flexible, testeable |
| **Observer / Events** | Spring Events + Twilio | Desacoplamiento de notificaciones asíncronas SMS (AFTER_COMMIT) |

---

## 4. Flujo de Procesamiento Completo: Crear Cita

**Ejemplo real del flujo de la aplicación:**

```
1. REQUEST: POST /api/v1/citas
   Headers: Authorization: Bearer <token>
   Body: {
     "idCliente": 1,
     "idEmpleado": 2,
     "idServicio": 3,
     "fechaInicio": "2026-04-15T10:00:00"
   }

2. CitaController.crearCita()
   - Valida JWT (JwtAuthenticationFilter)
   - Deserializa JSON → CrearCitaRequest
   - @Valid valida anotaciones (no nulo, etc.)

3. CrearCitaUseCase.execute()
   - Valida que el Cliente existe previamente via ClienteRepositoryPort (o lanza ClienteNoExistenteException)
   - Bloquea Empleado con lock pesimista para evitar carreras
   - Valida que Empleado está activo
   - Valida que Empleado está calificado para Servicio
   - Valida que fecha está en franja laboral del Empleado
   - Valida sin solapamiento con otras citas
   - Valida que hay recursos disponibles
   - Calcula fechaFin = fechaInicio + duracionServicio
   - Crea nueva entidad Cita
   - Asigna recursos via CitaRecursos
   - Publica CitaConfirmadaEvent mediante NotificacionEventPort

4. CitaPersistenceAdapter.guardar()
   - Mapea Cita (Domain) → CitaData (JPA)
   - CitaSpringDataRepository.save()
   - Persiste en PostgreSQL
   - Retorna CitaData guardada

5. Mapper: CitaData → Cita (Domain) → CitaResponse

6. RESPONSE: 201 Created
   (El cliente HTTP recibe respuesta inmediata)

7. Evento Asíncrono (Post-Commit):
   - TwilioSmsNotificationListener recibe CitaConfirmadaEvent tras confirmarse la transacción
   - Envía SMS automático vía Twilio al teléfono del cliente (+57) de forma asíncrona
   Body: {
     "idCita": 100,
     "idCliente": 1,
     "idEmpleado": 2,
     "idServicio": 3,
     "fechaInicio": "2026-04-15T10:00:00",
     "fechaFin": "2026-04-15T10:30:00",
     "estado": "CONFIRMADA",
     "recursosAsignados": [...]
   }
```

---

## 5. Convenciones de Codificación

### Nombres y Convenciones

```
✓ UseCases: {Verbo}{Sustantivo}UseCase.java
  CrearClienteUseCase, ActualizarClienteUseCase, BuscarClienteUseCase

✓ Entities: {Sustantivo}.java (sin sufijo)
  Cliente, Empleado, Cita, Recurso

✓ DTOs: {Sustantivo}{Tipo}Request/Response
  CrearClienteRequest, ClienteResponse, ActualizarCitaRequest

✓ Repositories: {Sustantivo}RepositoryPort / {Sustantivo}Repository
  ClienteRepositoryPort, ClienteSpringDataRepository

✓ Controllers: {Sustantivo}Controller
  ClienteController, CitaController

✓ Exceptions: {Sustantivo}{Condición}Exception
  ClienteNoEncontradoException, CitaExistenteException
```

### Validaciones

**En Application Layer (UseCase):**
```java
if (cliente == null) {
    throw new ClienteNoEncontradoException(idCliente);
}
```

**En Infrastructure Layer (Controller):**
```java
@Valid @RequestBody CrearClienteRequest request
// Anotaciones JSR-380: @NotNull, @Min, @Email, etc.
```

### Documentación

Todos los Controllers tienen anotaciones Swagger:

```java
@Operation(summary = "...", description = "...")
@ApiResponse(responseCode = "200", description = "...")
@Tag(name = "...", description = "...")
```

---

## 6. Independencia de Frameworks

### ¿Qué pasaría si cambiamos BD?

**Actual:** PostgreSQL via JPA

**Si usamos MongoDB:**
1. Crear `MongoClienteRepository implements ClienteRepositoryPort`
2. Cambiar `@Repository` en Adapter
3. El resto del código (Domain, Application) **NO CAMBIA**

### ¿Qué pasaría si quitamos Spring Security?

1. Reemplazar `SecurityConfig` con otro framework
2. Cambiar `JwtAuthenticationFilter`
3. El Domain y los UseCases **NO CAMBIAN**

**Esto es la esencia de Arquitectura Hexagonal.**

---

## 7. Recursos Adicionales

- 📚 [README.md](./README.md) - Visión general del proyecto
- 🚀 [QUICK_START.md](./QUICK_START.md) - Entrada rápida
- 🧭 [GETTING_STARTED.md](./GETTING_STARTED.md) - Ruta por perfil
- 📖 [DOCUMENTATION_INDEX.md](./DOCUMENTATION_INDEX.md) - Índice central
- 🔌 [API_REFERENCE.md](./API_REFERENCE.md) - Catálogo de endpoints

---

**Proyecto educativo SENA 2026** • v0.0.1-SNAPSHOT • Java 21


