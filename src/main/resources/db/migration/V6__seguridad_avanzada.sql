CREATE TABLE usuarios (
                          id_usuario BIGSERIAL PRIMARY KEY, -- Usamos id_usuario para ser explícitos
                          nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
                          contrasena VARCHAR(255) NOT NULL,
                          rol VARCHAR(20) NOT NULL,

    -- VINCULACIÓN CON DOMINIO (CRÍTICO)
                          id_empleado BIGINT UNIQUE, -- Un empleado tiene un usuario. Unique para 1a1.
                          CONSTRAINT fk_usuario_empleado FOREIGN KEY (id_empleado) REFERENCES empleados(id_empleado),

    -- MFA y Seguridad
                          mfa_enabled BOOLEAN DEFAULT FALSE,
                          mfa_secret VARCHAR(255),
                          codigos_recuperacion TEXT,
                          activo BOOLEAN DEFAULT TRUE
);