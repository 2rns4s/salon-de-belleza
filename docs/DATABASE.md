# Base de Datos - Salón de Belleza

![PostgreSQL 17](https://img.shields.io/badge/PostgreSQL-17-blue?style=flat-square) 
![Flyway](https://img.shields.io/badge/Migraciones-Flyway-brightgreen?style=flat-square)

**Última actualización:** Abril 2026

---

## 📋 Tabla de Contenidos

1. [Visión General](#visión-general)
2. [Migraciones Flyway](#migraciones-flyway)
3. [Modelos de Datos](#modelos-de-datos)
4. [Relaciones](#relaciones)
5. [Índices](#índices)
6. [Conexión](#conexión)
7. [Backup & Restore](#backup--restore)

---

## Visión General

**Base de datos:** PostgreSQL 17  
**ORM:** Spring Data JPA + Hibernate  
**Migraciones:** Flyway  
**Tipo de eliminación:** Soft delete (lógico) para ciertos tipos

### Características

- ✅ Migraciones versionadas (V1-V6)
- ✅ Integridad referencial (Foreign Keys)
- ✅ Índices para optimización
- ✅ Timestamps de auditoría
- ✅ Soft delete para auditoría

---

## Migraciones Flyway

### Ubicación

```
src/main/resources/db/migration/
```

### Migraciones en Orden

| Versión | Archivo | Tablas Creadas | Descripción |
|---------|---------|----------------|-------------|
| **V1** | `V1__crear_tabla_clientes.sql` | `clientes` | Registros de clientes |
| **V2** | `V2__crear_tabla_recursos.sql` | `recursos` | Inventario de recursos |
| **V3** | `V3__crear_tabla_clientes_y_serviciorecursos.sql` | `servicios`<br>`servicio_recursos` | Catálogo de servicios + relación M:N |
| **V4** | `V4__crear_tabla_empleados_franjas_laborales_y_calificaciones.sql` | `empleados`<br>`franjas_laborales`<br>`calificaciones` | Personal, horarios, skills |
| **V5** | `V5__crear_tabla_citas_y_detalle_recursos.sql` | `citas`<br>`citas_recursos` | Agendamientos + asignación de recursos |
| **V6** | `V6__seguridad_avanzada.sql` | `usuarios` | Autenticación, JWT, MFA |

**Ejecución automática:**
- Al iniciar la aplicación con `./gradlew bootRun`
- En tests si usa `testcontainers`
- Respeta orden: V1 → V2 → ... → V6

### Agregar Nueva Migración

1. Crear archivo en `src/main/resources/db/migration/`
2. Nombre: `V{número}__descripcion_snake_case.sql`
3. SQL con `CREATE TABLE`, `ALTER TABLE`, etc.
4. Usar convención de nombres: snake_case

**Ejemplo:**
```sql
-- V7__agregar_tabla_resenas.sql
CREATE TABLE resenas (
    id_resena SERIAL PRIMARY KEY,
    id_cliente BIGINT NOT NULL REFERENCES clientes(id_cliente),
    id_empleado BIGINT NOT NULL REFERENCES empleados(id_empleado),
    calificacion INT CHECK (calificacion BETWEEN 1 AND 5),
    comentario VARCHAR(500),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## Modelos de Datos

### Tabla: clientes

```sql
CREATE TABLE clientes (
    id_cliente SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    documento VARCHAR(10) NOT NULL UNIQUE,
    telefono VARCHAR(10),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

| Campo | Tipo | Notas |
|-------|------|-------|
| `id_cliente` | SERIAL | PK, auto-incremento |
| `nombre` | VARCHAR(100) | Requerido |
| `apellido` | VARCHAR(100) | Requerido |
| `documento` | VARCHAR(10) | UNIQUE, 8-10 dígitos |
| `telefono` | VARCHAR(10) | Formato: 3xxxxxxxxx |
| `fecha_registro` | TIMESTAMP | Auto, por defecto NOW() |

---

### Tabla: recursos

```sql
CREATE TABLE recursos (
    id_recurso SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    cantidad_total INT NOT NULL CHECK (cantidad_total > 0),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

| Campo | Tipo | Notas |
|-------|------|-------|
| `id_recurso` | SERIAL | PK |
| `nombre` | VARCHAR(100) | Requerido |
| `cantidad_total` | INT | > 0, se decrementa al asignar a citas |
| `fecha_creacion` | TIMESTAMP | Auto |

---

### Tabla: servicios

```sql
CREATE TABLE servicios (
    id_servicio SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    duracion INT NOT NULL CHECK (duracion > 0),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

| Campo | Tipo | Notas |
|-------|------|-------|
| `id_servicio` | SERIAL | PK |
| `nombre` | VARCHAR(100) | UNIQUE |
| `duracion` | INT | Minutos (ej: 30, 60, 90) |
| `fecha_creacion` | TIMESTAMP | Auto |

---

### Tabla: servicio_recursos

Relación M:N entre servicios y recursos.

```sql
CREATE TABLE servicio_recursos (
    id_servicio_recurso SERIAL PRIMARY KEY,
    id_servicio BIGINT NOT NULL REFERENCES servicios(id_servicio) ON DELETE CASCADE,
    id_recurso BIGINT NOT NULL REFERENCES recursos(id_recurso) ON DELETE CASCADE,
    nombre_recurso VARCHAR(100),
    cantidad_requerida INT NOT NULL CHECK (cantidad_requerida > 0),
    UNIQUE(id_servicio, id_recurso)
);
```

| Campo | Tipo | Notas |
|-------|------|-------|
| `id_servicio_recurso` | SERIAL | PK |
| `id_servicio` | BIGINT | FK a servicios |
| `id_recurso` | BIGINT | FK a recursos |
| `nombre_recurso` | VARCHAR(100) | Copia del nombre (desnormalización) |
| `cantidad_requerida` | INT | Cantidad de recursos que necesita el servicio |

---

### Tabla: empleados

```sql
CREATE TABLE empleados (
    id_empleado SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    telefono VARCHAR(10),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

| Campo | Tipo | Notas |
|-------|------|-------|
| `id_empleado` | SERIAL | PK |
| `nombre` | VARCHAR(100) | Requerido |
| `apellido` | VARCHAR(100) | Requerido |
| `correo` | VARCHAR(100) | UNIQUE, email |
| `telefono` | VARCHAR(10) | Formato: 3xxxxxxxxx |
| `activo` | BOOLEAN | Soft delete: false = inactivo |
| `fecha_creacion` | TIMESTAMP | Auto |

---

### Tabla: franjas_laborales

Horarios de trabajo de empleados.

```sql
CREATE TABLE franjas_laborales (
    id_franja_laboral SERIAL PRIMARY KEY,
    id_empleado BIGINT NOT NULL REFERENCES empleados(id_empleado) ON DELETE CASCADE,
    dia VARCHAR(20) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    CHECK (hora_fin > hora_inicio)
);
```

| Campo | Tipo | Notas |
|-------|------|-------|
| `id_franja_laboral` | SERIAL | PK |
| `id_empleado` | BIGINT | FK a empleados |
| `dia` | VARCHAR(20) | MONDAY, TUESDAY, ... SUNDAY |
| `hora_inicio` | TIME | HH:mm:ss |
| `hora_fin` | TIME | HH:mm:ss (> hora_inicio) |

---

### Tabla: calificaciones

Relación N:M entre empleados y servicios (qué servicios puede hacer cada empleado).

```sql
CREATE TABLE calificaciones (
    id_calificacion SERIAL PRIMARY KEY,
    id_empleado BIGINT NOT NULL REFERENCES empleados(id_empleado) ON DELETE CASCADE,
    id_servicio BIGINT NOT NULL REFERENCES servicios(id_servicio) ON DELETE CASCADE,
    UNIQUE(id_empleado, id_servicio)
);
```

| Campo | Tipo | Notas |
|-------|------|-------|
| `id_calificacion` | SERIAL | PK |
| `id_empleado` | BIGINT | FK a empleados |
| `id_servicio` | BIGINT | FK a servicios |
| | | UNIQUE: un empleado solo puede tener 1 calificación por servicio |

---

### Tabla: citas

```sql
CREATE TABLE citas (
    id_cita SERIAL PRIMARY KEY,
    id_cliente BIGINT NOT NULL REFERENCES clientes(id_cliente),
    id_empleado BIGINT NOT NULL REFERENCES empleados(id_empleado),
    id_servicio BIGINT NOT NULL REFERENCES servicios(id_servicio),
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADA',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (fecha_fin > fecha_inicio)
);

CREATE INDEX idx_cita_empleado_fecha ON citas(id_empleado, fecha_inicio);
CREATE INDEX idx_cita_cliente ON citas(id_cliente);
CREATE INDEX idx_cita_fecha ON citas(fecha_inicio);
```

| Campo | Tipo | Notas |
|-------|------|-------|
| `id_cita` | SERIAL | PK |
| `id_cliente` | BIGINT | FK a clientes |
| `id_empleado` | BIGINT | FK a empleados |
| `id_servicio` | BIGINT | FK a servicios |
| `fecha_inicio` | TIMESTAMP | ISO format |
| `fecha_fin` | TIMESTAMP | fecha_inicio + duración servicio |
| `estado` | VARCHAR(20) | CONFIRMADA, CANCELADA, REALIZADA, PENDIENTE |
| `fecha_creacion` | TIMESTAMP | Auto |

---

### Tabla: citas_recursos

Relación M:N entre citas y recursos asignados.

```sql
CREATE TABLE citas_recursos (
    id_cita_recurso SERIAL PRIMARY KEY,
    id_cita BIGINT NOT NULL REFERENCES citas(id_cita) ON DELETE CASCADE,
    id_recurso BIGINT NOT NULL REFERENCES recursos(id_recurso),
    UNIQUE(id_cita, id_recurso)
);

CREATE INDEX idx_cita_recurso_cita ON citas_recursos(id_cita);
```

| Campo | Tipo | Notas |
|-------|------|-------|
| `id_cita_recurso` | SERIAL | PK |
| `id_cita` | BIGINT | FK a citas (ON DELETE CASCADE) |
| `id_recurso` | BIGINT | FK a recursos |

---

### Tabla: usuarios

```sql
CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL DEFAULT 'RECEPTIONIST',
    id_empleado BIGINT REFERENCES empleados(id_empleado),
    mfa_enable BOOLEAN DEFAULT FALSE,
    mfa_secret VARCHAR(255),
    codigos_recuperacion VARCHAR(500),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_usuario_nombre ON usuarios(nombre_usuario);
```

| Campo | Tipo | Notas |
|-------|------|-------|
| `id_usuario` | SERIAL | PK |
| `nombre_usuario` | VARCHAR(50) | UNIQUE |
| `contrasena` | VARCHAR(255) | Hash BCrypt |
| `rol` | VARCHAR(20) | OWNER, RECEPTIONIST |
| `id_empleado` | BIGINT | FK opcional a empleados |
| `mfa_enable` | BOOLEAN | ¿Tiene MFA habilitado? |
| `mfa_secret` | VARCHAR(255) | Secret TOTP (si MFA activo) |
| `codigos_recuperacion` | VARCHAR(500) | Códigos backup (JSON array) |
| `activo` | BOOLEAN | Soft delete |
| `fecha_creacion` | TIMESTAMP | Auto |

---

## Relaciones

### Diagrama ER (Entidad-Relación)

```
Cliente 1──────N Cita N──────1 Empleado
                  │
                  └──────N CitaRecursos N──────1 Recurso

Empleado 1──────N FranjaLaboral
         1──────N Calificacion N──────1 Servicio

Servicio 1──────N ServicioRecursos N──────1 Recurso

Usuario N──────1 Empleado (opcional, nullable)
```

### Cascade Delete

| Tabla Padre | Tabla Hija | Comportamiento |
|-------------|-----------|---|
| `empleados` | `franjas_laborales` | DELETE CASCADE (borra franjas) |
| `empleados` | `calificaciones` | DELETE CASCADE |
| `empleados` | `citas` | Restringido (no se puede borrar empleado con citas) |
| `servicios` | `servicio_recursos` | DELETE CASCADE |
| `citas` | `citas_recursos` | DELETE CASCADE (libera recursos) |

### Soft Delete

Tablas que usan soft delete (`activo = false`):
- `empleados`
- `usuarios`

**Consultas deben filtrar:**
```sql
SELECT * FROM empleados WHERE activo = TRUE;
```

---

## Índices

### Índices Principales

| Tabla | Columnas | Tipo | Propósito |
|-------|----------|------|-----------|
| `citas` | `(id_empleado, fecha_inicio)` | INDEX | Búsqueda rápida de citas por empleado |
| `citas` | `(id_cliente)` | INDEX | Búsqueda por cliente |
| `citas` | `(fecha_inicio)` | INDEX | Búsqueda por rango de fechas |
| `citas_recursos` | `(id_cita)` | INDEX | Búsqueda de recursos por cita |
| `clientes` | `(documento)` | UNIQUE | Garantizar unicidad |
| `empleados` | `(correo)` | UNIQUE | Garantizar unicidad |
| `servicios` | `(nombre)` | UNIQUE | Garantizar unicidad |
| `usuarios` | `(nombre_usuario)` | UNIQUE | Garantizar unicidad |

### Crear Índices Adicionales (si es necesario)

```sql
-- Para búsquedas frecuentes por estado de cita
CREATE INDEX idx_cita_estado ON citas(estado);

-- Para reportes por fecha rango
CREATE INDEX idx_cita_fecha_estado ON citas(fecha_inicio, estado);
```

---

## Conexión

### Docker Compose

```yaml
# docker-compose.yml
version: '3.8'

services:
  postgres:
    image: postgres:17-alpine
    environment:
      POSTGRES_DB: salon_de_belleza_db
      POSTGRES_USER: salon_admin
      POSTGRES_PASSWORD: salon_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

### Variables de Conexión

```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/salon_de_belleza_db
spring.datasource.username=salon_admin
spring.datasource.password=salon_password
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
spring.flyway.locations=classpath:/db/migration
```

### Conexión desde Cliente

```bash
# Usando psql
psql -h localhost -U salon_admin -d salon_de_belleza_db
# Password: salon_password

# Usando pgAdmin (UI)
# URL: http://localhost:5050
# (si la incluyes en docker-compose)
```

---

## Backup & Restore

### Crear Backup

```bash
# Backup completo
pg_dump -h localhost -U salon_admin -d salon_de_belleza_db > backup.sql

# Backup comprimido
pg_dump -h localhost -U salon_admin -d salon_de_belleza_db | gzip > backup.sql.gz
```

### Restaurar

```bash
# Desde archivo SQL
psql -h localhost -U salon_admin -d salon_de_belleza_db < backup.sql

# Desde archivo comprimido
gunzip -c backup.sql.gz | psql -h localhost -U salon_admin -d salon_de_belleza_db
```

### Realizar Backup con Docker

```bash
# Mientras Docker está corriendo
docker-compose exec postgres pg_dump -U salon_admin salon_de_belleza_db > backup.sql

# Restaurar en Docker
docker-compose exec -T postgres psql -U salon_admin salon_de_belleza_db < backup.sql
```

---

## Consultas Útiles

### Obtener Estadísticas

```sql
-- Total de clientes
SELECT COUNT(*) as total_clientes FROM clientes;

-- Citas por estado
SELECT estado, COUNT(*) FROM citas GROUP BY estado;

-- Empleados más ocupados
SELECT e.nombre, COUNT(c.id_cita) as total_citas
FROM empleados e
LEFT JOIN citas c ON e.id_empleado = c.id_empleado
WHERE c.fecha_inicio >= CURRENT_DATE
GROUP BY e.id_empleado, e.nombre
ORDER BY total_citas DESC;

-- Recursos más utilizados
SELECT r.nombre, COUNT(cr.id_cita_recurso) as usos
FROM recursos r
LEFT JOIN citas_recursos cr ON r.id_recurso = cr.id_recurso
GROUP BY r.id_recurso, r.nombre
ORDER BY usos DESC;
```

### Mantenimiento

```sql
-- Eliminar citas antiguas (más de 1 año)
DELETE FROM citas WHERE fecha_inicio < NOW() - INTERVAL '1 year';

-- Ver tamaño de tablas
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

-- Vacuum (mantenimiento)
VACUUM ANALYZE;
```

---

**Última actualización:** Abril 2026 | **Versión:** 0.0.1-SNAPSHOT


