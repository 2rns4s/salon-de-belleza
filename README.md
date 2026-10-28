# 💇‍♀️ Salón de Belleza - API de Gestión

![Java 21](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk) 
![Spring Boot 3.5.7](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen?style=flat-square&logo=spring-boot) 
![PostgreSQL 17](https://img.shields.io/badge/PostgreSQL-17-blue?style=flat-square&logo=postgresql)
![Hexagonal](https://img.shields.io/badge/Architecture-Hexagonal-purple?style=flat-square)
![Status](https://img.shields.io/badge/Status-Development-yellow?style=flat-square)

**Última actualización:** Octubre 2026 | **Versión:** 0.0.1-SNAPSHOT

API REST profesional con **Arquitectura Hexagonal** para la gestión integral de salones de belleza: clientes, empleados, servicios, recursos, citas y autenticación JWT + MFA.

---

## 📋 Tabla de Contenidos

1. [Quick Start](#-quick-start)
2. [Documentación](#-documentación)
3. [Características](#-características)
4. [Requisitos](#-requisitos)
5. [Stack Tecnológico](#-stack-tecnológico)
6. [Estructura del Proyecto](#-estructura-del-proyecto)
7. [Comandos Útiles](#-comandos-útiles)
8. [Licencia](#-licencia)

---

## 🚀 Quick Start

### Inicialización en 3 pasos:

```bash
# 1️⃣ Clonar y preparar
git clone <repo>
cd SalonDeBelleza

# 2️⃣ Levantar PostgreSQL (con Docker Compose)
docker-compose up -d

# 3️⃣ Ejecutar aplicación
./gradlew bootRun
```

**Acceso a la API:**
- 🌐 **API Base:** http://localhost:8080/api/v1
- 📖 **Swagger UI:** http://localhost:8080/swagger-ui.html
- 📄 **OpenAPI JSON:** http://localhost:8080/v3/api-docs

---

## 📚 Documentación

| Documento | Propósito | Audiencia |
|-----------|-----------|-----------|
| [**ARCHITECTURE.md**](./ARCHITECTURE.md) | Visión general, capas, patrones | Arquitectos, Senior Devs |
| [**API_REFERENCE.md**](./API_REFERENCE.md) | Catálogo de endpoints, DTOs, ejemplos | Frontend Devs, QA |
| [**docs/SECURITY.md**](./docs/SECURITY.md) | Autenticación, JWT y MFA | Backend, Frontend |
| [**docs/DATABASE.md**](./docs/DATABASE.md) | Modelo de datos y migraciones | Backend, DBA |
| [**docs/TESTING.md**](./docs/TESTING.md) | Estrategia y ejecución de pruebas | QA, Backend |
| [**docs/DEPLOYMENT.md**](./docs/DEPLOYMENT.md) | Ejecución y despliegue | DevOps, Backend |
| [**GETTING_STARTED.md**](./GETTING_STARTED.md) | Guía de arranque por rol | Todos |
| [**QUICK_START.md**](./QUICK_START.md) | Acceso rápido a documentación | Todos |
| [**DOCUMENTATION_INDEX.md**](./DOCUMENTATION_INDEX.md) | Índice central de navegación | Todos |

---

## ✨ Características

### 🏗️ Arquitectura
- **Hexagonal (Ports & Adapters):** Separa lógica de negocio de frameworks
- **Capas bien definidas:** Domain → Application → Infrastructure
- **Independencia de frameworks:** Cambiar BD, frameworks sin afectar negocio

### 🔐 Seguridad
- **JWT (JSON Web Tokens):** Autenticación stateless
- **MFA (Multi-Factor Authentication):** TOTP con Google Authenticator
- **BCrypt:** Encriptación de contraseñas
- **Cifrado en BD (AES-256):** Cifrado transparente para PII sensible (documento y teléfono de clientes)
- **Spring Security:** Autorización basada en roles (OWNER, RECEPTIONIST)
- **Soft Delete:** Eliminación lógica para auditoría

### 🔔 Notificaciones
- **SMS Automático (Twilio):** Envío asíncrono de confirmaciones al teléfono del cliente tras agendar cita
- **Event-Driven Architecture:** Eventos desacoplados ejecutados tras el commit de transacción (AFTER_COMMIT)

### 📊 Gestión Completa
- **Clientes:** Registro, búsqueda, historial de citas
- **Empleados:** Perfiles, franjas laborales, calificaciones, skills
- **Servicios:** Catálogo, duración, recursos requeridos
- **Recursos:** Inventario, disponibilidad, asignación a citas
- **Citas:** Agendamiento inteligente, validaciones automáticas
- **Disponibilidad:** Consulta horarios libres en tiempo real

### ✔️ Validaciones Automáticas
- Empleado activo y calificado para el servicio
- Horario dentro de franja laboral
- Sin solapamiento de citas
- Recursos disponibles en cantidad suficiente
- Fecha futura válida

### 📖 API First
- **Swagger/OpenAPI:** Documentación interactiva
- **DTOs:** Contrato de entrada/salida explícito
- **Error handling:** Respuestas consistentes con detalles

---

## 🛠️ Requisitos

### Mínimos
- **Java 21+** (OpenJDK o similar)
- **Docker & Docker Compose** (para PostgreSQL)
- **Gradle 8.x** (incluido en el proyecto)

### Recomendados
- **IDE:** IntelliJ IDEA, VS Code, Eclipse
- **Cliente HTTP:** Postman, Insomnia, Thunder Client
- **Git:** Para versionado

---

## 📦 Stack Tecnológico

### Backend Core
- **Java 21** - Lenguaje de programación
- **Spring Boot 3.5.7** - Framework web
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - ORM y persistencia
- **Gradle 8.x** - Build tool

### Base de Datos & Cache
- **PostgreSQL 17** - Base de datos principal
- **Flyway** - Migraciones de BD versionadas
- **Redis** (opcional) - Cache distribuido

### Bibliotecas Especializadas
- **MapStruct 1.6.3** - Mapeo automático DTO ↔ Entity
- **jjwt (0.13.0)** - Generación/validación de JWT
- **TOTP (1.7.1)** - MFA con códigos TOTP
- **Twilio SDK (11.3.6)** - Integración de notificaciones SMS
- **Lombok** - Reducción de boilerplate (@Getter, @Setter)

### Documentación & Testing
- **SpringDoc OpenAPI 2.8.14** - Swagger/OpenAPI automático
- **JUnit 5** - Framework de testing
- **Testcontainers** - BD real en tests
- **Mockito** - Mocks para testing

---

## 📂 Estructura del Proyecto

```
SalonDeBelleza/
├── src/
│   ├── main/
│   │   ├── java/com/salondebelleza/salondebelleza/
│   │   │   ├── SalonDeBellezaApplication.java
│   │   │   └── agendamiento/
│   │   │       ├── domain/                    # ⭐ LÓGICA DE NEGOCIO PURA
│   │   │       │   ├── entities/              # 10 entidades del dominio
│   │   │       │   ├── port/                  # 7 interfaces (Repository Ports + NotificacionEventPort)
│   │   │       │   ├── exception/             # 17 excepciones de negocio
│   │   │       │   └── valueobjects/          # FranjaLaboral, Rol
│   │   │       │
│   │   │       ├── application/               # 🎯 CASOS DE USO (ORQUESTACIÓN)
│   │   │       │   ├── usecase/               # 36 UseCases organizados por módulo
│   │   │       │   ├── event/                 # CitaConfirmadaEvent
│   │   │       │   ├── dto/                   # Request/Response DTOs
│   │   │       │   └── mapper/                # 6 mappers (DTO ↔ Entity)
│   │   │       │
│   │   │       └── infrastructure/            # 🔌 IMPLEMENTACIÓN
│   │   │           ├── adapter/
│   │   │           │   ├── web/               # 9 Controllers (REST)
│   │   │           │   ├── persistence/       # 6 Adapters + JPA Repos + CryptoConverter
│   │   │           │   ├── notification/      # TwilioSmsNotificationListener
│   │   │           │   └── event/             # SpringEventPublisherAdapter
│   │   │           └── config/                # Security, OpenAPI, TOTP, AsyncConfig
│   │   │
│   │   └── resources/
│   │       ├── application.properties         # Config principal
│   │       ├── application-test.properties    # Config tests
│   │       └── db/migration/                  # Migraciones Flyway (V1-V6, V8)
│   │
│   └── test/
│       └── java/com/salondebelleza/salondebelleza/
│           └── (Test files por Controller)
│
├── build.gradle                  # Dependencias y configuración Gradle
├── gradle/wrapper/               # Gradle wrapper (ejecutable)
├── docker-compose.yml            # PostgreSQL + configuración
├── Dockerfile                    # Imagen Docker de la app
│
├── docs/                         # Documentación de soporte
│   ├── DATABASE.md
│   ├── DEPLOYMENT.md
│   ├── SECURITY.md
│   └── TESTING.md
│
├── README.md                     # Este archivo
├── QUICK_START.md                # Atajos de lectura
├── GETTING_STARTED.md            # Guía por rol
├── DOCUMENTATION_INDEX.md        # Índice de documentación
├── ARCHITECTURE.md               # Arquitectura
└── API_REFERENCE.md              # Referencia API
```

### Estructura de Capas (Hexagonal)

```
┌─────────────────────────────────┐
│    Controllers (REST)            │ ← Entrada HTTP
│    GlobalExceptionHandler        │
└──────────────┬──────────────────┘
               │
┌──────────────▼──────────────────┐
│    UseCases (Orquestación)       │ ← 36 casos de uso
│    DTOs & Mappers               │
└──────────────┬──────────────────┘
               │
┌──────────────▼──────────────────┐
│    Entities & Ports              │ ← Contrato/interfaz
│    Excepciones de negocio        │
└──────────────┬──────────────────┘
               │
┌──────────────▼──────────────────┐
│    Persistence Adapters (JPA)    │ ← Salida a BD
│    Spring Data Repositories      │
└──────────────┬──────────────────┘
               │
               ▼
          PostgreSQL
```

---

## 🔧 Comandos Útiles

### Desarrollo

```bash
# Ejecutar aplicación
./gradlew bootRun

# Compilar sin ejecutar
./gradlew build

# Ejecutar tests
./gradlew test

# Generar reporte de tests
./gradlew test --info

# Ejecutar solo tests de integración
./gradlew test --tests "*ControllerTest"
```

### Docker & Base de Datos

```bash
# Levantar PostgreSQL
docker-compose up -d

# Detener PostgreSQL
docker-compose down

# Ver logs de PostgreSQL
docker-compose logs -f

# Construir imagen Docker de la app
docker build -t salon-belleza:0.0.1 .

# Ejecutar app en contenedor
docker run -p 8080:8080 --env-file .env salon-belleza:0.0.1
```

### Limpiar

```bash
# Limpiar build anterior
./gradlew clean

# Limpiar + rebuild
./gradlew clean build

# Forzar descarga de dependencias
./gradlew --refresh-dependencies build
```

---

## 🔗 Endpoints Principales

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/api/v1/auth/login` | Iniciar sesión | ❌ |
| POST | `/api/v1/auth/verify-mfa` | Verificar MFA (TOTP) | ❌ (token pre-auth) |
| GET | `/api/v1/clientes` | Listar clientes | ✅ |
| POST | `/api/v1/clientes` | Crear cliente | ✅ |
| GET | `/api/v1/empleados` | Listar empleados | ✅ |
| POST | `/api/v1/empleados` | Crear empleado | ✅ |
| GET | `/api/v1/citas` | Listar citas | ✅ |
| POST | `/api/v1/citas` | Crear cita | ✅ |
| GET | `/api/v1/disponibilidad` | Consultar horarios libres | ✅ |
| GET | `/api/v1/dashboard` | Dashboard operativo por rango de fechas | ✅ OWNER |
| POST | `/api/v1/usuarios` | Crear usuario | ✅ OWNER |

**Base URL:** `http://localhost:8080/api/v1`

**Auth Header:** `Authorization: Bearer <jwt_token>`

👉 Ver [API_REFERENCE.md](./API_REFERENCE.md) para referencia completa con ejemplos.

---

## 📜 Licencia

Proyecto educativo SENA 2026. Licencia educativa - Uso académico autorizado.

---

## 📝 Notas de Versión

### v0.0.1-SNAPSHOT (Actual)
- ✅ Arquitectura Hexagonal completa
- ✅ CRUD completo (8 entidades)
- ✅ Autenticación JWT + MFA (TOTP)
- ✅ Cifrado en BD (AES-256)
- ✅ Notificaciones automáticas por SMS (Twilio)
- ✅ Validaciones automáticas de citas
- ✅ Swagger/OpenAPI
- ✅ Tests de integración
- ✅ Docker + docker-compose

**Próximas mejoras:**
- [x] Notificaciones por SMS (Twilio)
- [ ] Reporte de ingresos

---

**¡Gracias por usar Salón de Belleza API!** 💇‍♀️✨

Última actualización: Octubre 2026
