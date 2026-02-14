# Sistema de Gestión de Salón de Belleza

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen?style=flat-square&logo=spring-boot) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?style=flat-square&logo=postgresql)

API REST con **Arquitectura Hexagonal** para gestión de salones de belleza: clientes, empleados, servicios, recursos, citas y autenticación JWT + MFA.

📚 [Documentación Técnica](DOCUMENTACION.md) • 🎨 [Manual Frontend](MANUAL_FRONTEND.md) • [Swagger UI](http://localhost:8080/swagger-ui.html)

## Inicio Rápido

```bash
# 1. Levantar PostgreSQL
docker-compose up -d

# 2. Ejecutar aplicación
./gradlew bootRun
```

**Acceso:** http://localhost:8080 | **Swagger:** http://localhost:8080/swagger-ui.html

## Arquitectura Hexagonal

```
Infrastructure → Controllers, JPA Repositories, Security
Application    → Use Cases, DTOs, Mappers
Domain         → Entities, Ports (interfaces), Exceptions
```

**Capas**: Domain (core) ← Application (casos de uso) ← Infrastructure (adaptadores)

## Autenticación JWT + MFA

**Login:** `POST /api/v1/auth/login` → Retorna token (o pre-auth si MFA habilitado)
**MFA:** `POST /api/v1/auth/verify-mfa` → Valida código TOTP y retorna token final
**Uso:** Incluir `Authorization: Bearer {token}` en todos los endpoints protegidos

## Endpoints (Base: `/api/v1`)

| Recurso | Endpoint | Operaciones |
|---------|----------|-------------|
| Auth | `/auth` | Login, MFA |
| Clientes | `/clientes` | GET (listar, ID, buscar), POST, PUT, DELETE |
| Empleados | `/empleados` | GET (listar, ID), POST, PUT, DELETE |
| Servicios | `/servicios` | GET (listar, ID, buscar), POST, PUT, DELETE |
| Recursos | `/recursos` | GET (listar, ID, buscar), POST, PUT, DELETE |
| Citas | `/citas` | GET (listar, ID), POST, PUT, DELETE |
| Usuarios | `/usuarios` | GET (listar, ID), POST, PUT, DELETE (solo OWNER) |
| Disponibilidad | `/disponibilidad` | GET (consultar horarios libres) |

## Stack Tecnológico

**Core:** Spring Boot 3.5.7, Spring Security, JPA + PostgreSQL 17
**Seguridad:** JWT (jjwt), TOTP, BCrypt
**Mapeo:** MapStruct 1.6.3, Lombok
**Docs:** SpringDoc OpenAPI 2.8.14
**Testing:** JUnit 5, Testcontainers
**Build:** Gradle 8.x

## Base de Datos

**PostgreSQL 17** con migraciones Flyway automáticas:
- V1: `clientes` | V2: `recursos` | V3: `servicios` + relaciones
- V4: `empleados`, `franjas_laborales`, `calificaciones`
- V5: `citas`, `citas_recursos` | V6: `usuarios` (MFA)

**Conexión:** `postgresql://localhost:5432/salon_de_belleza_db` (salon_admin/salon_password)

## Estructura del Proyecto

```
src/main/java/com/.../agendamiento/
├── domain/              # Entities, Ports, Exceptions, ValueObjects
├── application/         # UseCases, DTOs, Mappers
└── infrastructure/      # Controllers, Repositories, Config, Security
```

**8 Controllers** (Auth, Cliente, Empleado, Servicio, Recurso, Cita, Usuario, Disponibilidad)
**6 Ports** (interfaces de repositorio) | **6 Adapters** (implementaciones JPA)
**28 UseCases** (CRUD completo por entidad) | **2 Mappers** por entidad (DTO + Persistence)

## Comandos Útiles

```bash
./gradlew build          # Compilar
./gradlew test           # Tests
./gradlew bootRun        # Ejecutar
docker-compose up -d     # PostgreSQL
docker-compose down      # Detener DB
```

---

**Proyecto educativo SENA 2026** • v0.0.1-SNAPSHOT • Java 21
