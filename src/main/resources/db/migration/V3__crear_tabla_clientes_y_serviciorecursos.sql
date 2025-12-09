CREATE TABLE servicios
(
    id_servicio BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    duracion    INTEGER      NOT NULL
);

CREATE TABLE servicio_recursos (
    id_servicio_recursos BIGSERIAL PRIMARY KEY,
    cantidad_requerida  INTEGER      NOT NULL,
    id_servicio         BIGINT       NOT NULL,
    id_recurso           BIGINT       NOT NULL,
    CONSTRAINT fk_servicio_recursos_servicio
        FOREIGN KEY (id_servicio)
            REFERENCES servicios(id_servicio)
            ON DELETE CASCADE,

    CONSTRAINT fk_servicio_recursos_recurso
        FOREIGN KEY (id_recurso)
            REFERENCES recursos(id_recurso)
)