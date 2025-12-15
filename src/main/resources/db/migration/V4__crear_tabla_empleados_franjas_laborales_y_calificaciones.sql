-- 1. Tabla EMPLEADOS
-- Se usa 'correo' para consistencia con tu Dominio y DTOs
CREATE TABLE empleados
(
    id_empleado BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    apellido    VARCHAR(100) NOT NULL,
    correo      VARCHAR(100) NOT NULL UNIQUE,
    telefono    VARCHAR(20)  NOT NULL
);

-- 2. Tabla FRANJA_LABORAL
-- Relación 1:N (Un empleado tiene muchos horarios)
CREATE TABLE franjas_laborales
(
    id_franja_laboral BIGSERIAL PRIMARY KEY,
    id_empleado       BIGINT      NOT NULL,
    dia               VARCHAR(20) NOT NULL, -- Se guarda el ENUM como texto: "MONDAY", "TUESDAY", etc.
    hora_inicio       TIME        NOT NULL,
    hora_fin          TIME        NOT NULL,

    -- Constraint: Si borras al empleado, se borran sus horarios automáticamente
    FOREIGN KEY (id_empleado) REFERENCES empleados (id_empleado) ON DELETE CASCADE
);

-- 3. Tabla CALIFICACION (Tabla intermedia Empleado <-> Servicio)
-- Relación N:M desglosada con ID propio según tu diagrama
CREATE TABLE calificaciones
(
    id_calificacion BIGSERIAL PRIMARY KEY,
    id_empleado       BIGINT NOT NULL,
    id_servicio       BIGINT NOT NULL,

    -- Constraints de integridad referencial
    FOREIGN KEY (id_empleado) REFERENCES empleados (id_empleado) ON DELETE CASCADE,
    FOREIGN KEY (id_servicio) REFERENCES servicios (id_servicio) ON DELETE CASCADE,

    -- Regla de Negocio: Un empleado no puede tener repetido el mismo servicio
    -- (Evita duplicados técnicos)
    UNIQUE (id_empleado, id_servicio)
);