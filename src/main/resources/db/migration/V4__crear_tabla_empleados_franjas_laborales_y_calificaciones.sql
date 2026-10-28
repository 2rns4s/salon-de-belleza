-- 1. Tabla EMPLEADOS
CREATE TABLE empleados
(
    id_empleado BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    apellido    VARCHAR(100) NOT NULL,
    correo      VARCHAR(100) NOT NULL UNIQUE,
    telefono    VARCHAR(20)  NOT NULL
);

-- 2. Tabla FRANJA_LABORAL
CREATE TABLE franjas_laborales
(
    id_franja_laboral BIGSERIAL PRIMARY KEY,
    id_empleado       BIGINT      NOT NULL,
    dia               VARCHAR(20) NOT NULL,
    hora_inicio       TIME        NOT NULL,
    hora_fin          TIME        NOT NULL,

    FOREIGN KEY (id_empleado) REFERENCES empleados (id_empleado) ON DELETE CASCADE
);

-- 3. Tabla CALIFICACION (Tabla intermedia Empleado <-> Servicio)
CREATE TABLE calificaciones
(
    id_calificacion BIGSERIAL PRIMARY KEY,
    id_empleado       BIGINT NOT NULL,
    id_servicio       BIGINT NOT NULL,

    FOREIGN KEY (id_empleado) REFERENCES empleados (id_empleado) ON DELETE CASCADE,
    FOREIGN KEY (id_servicio) REFERENCES servicios (id_servicio) ON DELETE CASCADE,


    UNIQUE (id_empleado, id_servicio)
);