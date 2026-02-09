# 💇 Sistema de Gestión de Salón de Belleza

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen?style=flat-square&logo=spring-boot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?style=flat-square&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=flat-square&logo=docker)

**API REST completa para la gestión integral de salones de belleza**

[Documentación Completa](DOCUMENTACION.md) • [API Docs (Swagger)](#-documentación-api)

</div>

---

## 📖 Sobre el Proyecto

Sistema backend RESTful desarrollado con **Spring Boot 3.5.7** que implementa **Arquitectura Hexagonal** y **Clean Architecture** para la gestión completa de un salón de belleza, incluyendo:

- 👥 Gestión de clientes, empleados y usuarios
- 💇 Catálogo de servicios y recursos
- 📅 Sistema de agendamiento de citas con validaciones inteligentes
- 🔐 Autenticación JWT + MFA (Multi-Factor Authentication)
- 📊 Control de disponibilidad y asignación de recursos

## 🚀 Inicio Rápido

### Prerrequisitos

- **Java 21** o superior
- **Docker** y **Docker Compose**
- **Gradle 8.x** (incluido con Gradle Wrapper)

### Instalación

```bash
# 1. Clonar el repositorio
git clone <url-del-repositorio>
cd SalonDeBelleza

# 2. Levantar PostgreSQL con Docker Compose
docker-compose up -d

# 3. Compilar y ejecutar la aplicación
./gradlew bootRun

# La aplicación estará disponible en: http://localhost:8080
```

### Acceso a la Documentación

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────────────────┐
│                   INFRASTRUCTURE                         │
│  (Controllers, Repositories, Security Config)           │
├─────────────────────────────────────────────────────────┤
│                    APPLICATION                           │
│      (Use Cases, DTOs, Mappers)                         │
├─────────────────────────────────────────────────────────┤
│                      DOMAIN                              │
│  (Entities, Value Objects, Ports, Exceptions)           │
└─────────────────────────────────────────────────────────┘
```

**Patrón**: Arquitectura Hexagonal (Ports & Adapters) + Clean Architecture

## 🔐 Autenticación

El sistema utiliza **JWT + MFA (TOTP)** para autenticación segura:

### Login sin MFA (Usuario RECEPTIONIST)
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"nombreUsuario": "recepcionista", "contrasena": "password"}'
```

### Login con MFA (Usuario OWNER)
```bash
# Paso 1: Login inicial
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"nombreUsuario": "propietario", "contrasena": "password"}'

# Paso 2: Verificar código TOTP
curl -X POST http://localhost:8080/api/v1/auth/verify-mfa \
  -H "Content-Type: application/json" \
  -d '{"nombreUsuario": "propietario", "totpCode": "123456", "preAuthToken": "..."}'
```

## 📡 Endpoints Principales

| Recurso | Endpoint Base | Descripción |
|---------|---------------|-------------|
| 🔐 Auth | `/api/v1/auth` | Login y verificación MFA |
| 👤 Clientes | `/api/v1/clientes` | CRUD de clientes |
| 👨‍💼 Empleados | `/api/v1/empleados` | CRUD de empleados |
| 💇 Servicios | `/api/v1/servicios` | CRUD de servicios |
| 🧴 Recursos | `/api/v1/recursos` | CRUD de recursos |
| 📅 Citas | `/api/v1/citas` | CRUD de citas |
| 🔑 Usuarios | `/api/v1/usuarios` | CRUD de usuarios |

Todos los endpoints (excepto `/auth/**`) requieren token JWT en el header:
```
Authorization: Bearer {tu-token-jwt}
```

## 🛠️ Tecnologías

- **Framework**: Spring Boot 3.5.7, Spring Security, Spring Data JPA
- **Base de Datos**: PostgreSQL + Flyway (migraciones)
- **Seguridad**: JWT (jjwt), TOTP (totp), BCrypt
- **Mapeo**: MapStruct 1.6.3, Lombok
- **Documentación**: SpringDoc OpenAPI 2.8.14
- **Testing**: JUnit 5, Testcontainers
- **Build**: Gradle 8.x

## 📦 Comandos Útiles

```bash
# Compilar proyecto
./gradlew build

# Ejecutar tests
./gradlew test

# Generar JAR
./gradlew bootJar

# Limpiar build
./gradlew clean

# Levantar PostgreSQL
docker-compose up -d

# Ver logs de PostgreSQL
docker-compose logs -f postgres

# Detener contenedores
docker-compose down
```

## 📊 Base de Datos

### Migraciones Flyway (Automáticas)

- **V1**: Tabla `clientes`
- **V2**: Tabla `recursos`
- **V3**: Tablas `servicios` y `servicio_recursos`
- **V4**: Tablas `empleados`, `franjas_laborales`, `calificaciones`
- **V5**: Tablas `citas` y `citas_recursos`
- **V6**: Tabla `usuarios` (con soporte MFA)

### Configuración de PostgreSQL

```properties
URL: jdbc:postgresql://localhost:5432/salon_de_belleza_db
Usuario: salon_admin
Contraseña: salon_password
```

## 🎨 Estructura del Proyecto

```
src/main/java/com/salondebelleza/salondebelleza/
├── SalonDeBellezaApplication.java
└── agendamiento/
    ├── domain/              # Entidades, puertos, excepciones
    ├── application/         # Casos de uso, DTOs, mappers
    └── infrastructure/      # Controllers, repositories, config
```

## 📚 Documentación Completa

Para ver la documentación exhaustiva del proyecto, consulta: **[DOCUMENTACION.md](DOCUMENTACION.md)**

Incluye:
- 📂 Estructura detallada de carpetas
- 💾 Diagrama completo de base de datos
- 🔐 Arquitectura de seguridad JWT + MFA
- 🎨 Patrones de diseño implementados
- ❓ FAQ y solución de problemas
- 🚀 Roadmap de mejoras futuras
- 📖 Glosario de términos técnicos

## 👥 Equipo de Desarrollo

- **Desarrollador**: Daniel
- **Organización**: SENA
- **Proyecto**: Sistema de Gestión de Salón de Belleza

## 📄 Licencia

Proyecto educativo - SENA 2026

---

<div align="center">

**Estado del Proyecto**: 🟢 En desarrollo activo

**Versión**: 0.0.1-SNAPSHOT | **Documentación**: v3.0 | **Última actualización**: Enero 22, 2026

</div>
