-- 1. Tabla Principal: CITAS
CREATE TABLE citas (
                       id_cita BIGSERIAL PRIMARY KEY,
                       id_cliente BIGINT NOT NULL,
                       id_empleado BIGINT NOT NULL,
                       id_servicio BIGINT NOT NULL,
                       fecha_inicio TIMESTAMP NOT NULL,
                       fecha_fin TIMESTAMP NOT NULL,
                       estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
                       fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT fk_citas_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
                       CONSTRAINT fk_citas_empleado FOREIGN KEY (id_empleado) REFERENCES empleados(id_empleado),
                       CONSTRAINT fk_citas_servicio FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio)
);

-- 2. Tabla de Detalle: CITA_RECURSOS
CREATE TABLE citas_recursos (
                               id_cita_recurso BIGSERIAL PRIMARY KEY,
                               id_cita BIGINT NOT NULL,
                               id_recurso BIGINT NOT NULL,

                               CONSTRAINT fk_citarecursos_cita FOREIGN KEY (id_cita) REFERENCES citas(id_cita) ON DELETE CASCADE,
                               CONSTRAINT fk_citarecursos_recurso FOREIGN KEY (id_recurso) REFERENCES recursos(id_recurso)
);

CREATE INDEX idx_citas_empleado_fechas ON citas(id_empleado, fecha_inicio, fecha_fin);
CREATE INDEX idx_citarecursos_recurso ON citas_recursos(id_recurso);