-- 1. Tabla Principal: CITAS
CREATE TABLE citas (
                       id_cita BIGSERIAL PRIMARY KEY,

    -- Relaciones principales (Foreign Keys)
                       id_cliente BIGINT NOT NULL,
                       id_empleado BIGINT NOT NULL,
                       id_servicio BIGINT NOT NULL,

    -- Gestión del Tiempo
    -- Usamos TIMESTAMP para manejar Fecha + Hora en una sola columna,
    -- facilitando las consultas de rangos y solapamientos.
                       fecha_inicio TIMESTAMP NOT NULL,
                       fecha_fin TIMESTAMP NOT NULL,

    -- Ciclo de Vida (PENDIENTE, CONFIRMADA, CANCELADA, REALIZADA)
                       estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',

    -- Auditoría automática
                       fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Restricciones de Integridad
                       CONSTRAINT fk_citas_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
                       CONSTRAINT fk_citas_empleado FOREIGN KEY (id_empleado) REFERENCES empleados(id_empleado),
                       CONSTRAINT fk_citas_servicio FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio)
);

-- 2. Tabla de Detalle: CITA_RECURSOS
-- Esta tabla materializa la asignación automática: "Para la Cita X, se reservó el Recurso Y"
CREATE TABLE citas_recursos (
                               id_cita_recurso BIGSERIAL PRIMARY KEY,
                               id_cita BIGINT NOT NULL,
                               id_recurso BIGINT NOT NULL, -- El recurso físico real (ej: Silla #5)

    -- Si se borra la cita, liberamos las reservas de recursos automáticamente
                               CONSTRAINT fk_citarecursos_cita FOREIGN KEY (id_cita) REFERENCES citas(id_cita) ON DELETE CASCADE,
                               CONSTRAINT fk_citarecursos_recurso FOREIGN KEY (id_recurso) REFERENCES recursos(id_recurso)
);

-- Índices recomendados para optimizar las búsquedas de disponibilidad
-- (Ayuda a que las queries de solapamiento sean rápidas)
CREATE INDEX idx_citas_empleado_fechas ON citas(id_empleado, fecha_inicio, fecha_fin);
CREATE INDEX idx_citarecursos_recurso ON citas_recursos(id_recurso);