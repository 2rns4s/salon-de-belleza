CREATE TABLE usuarios (
                          id_usuario BIGSERIAL PRIMARY KEY,
                          nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
                          contrasena VARCHAR(255) NOT NULL,
                          rol VARCHAR(20) NOT NULL,
                          id_empleado BIGINT UNIQUE,
                          CONSTRAINT fk_usuario_empleado FOREIGN KEY (id_empleado) REFERENCES empleados(id_empleado),
                          mfa_enabled BOOLEAN DEFAULT FALSE,
                          mfa_secret VARCHAR(255),
                          codigos_recuperacion TEXT,
                          activo BOOLEAN DEFAULT TRUE
);