package com.salondebelleza.salondebelleza.agendamiento.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos de autenticación del usuario para iniciar sesión")
public record LoginRequest(
        @Schema(
                description = "Nombre de usuario único del usuario",
                example = "admin",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El nombre de usuario es obligatorio")
        String nombreUsuario,

        @Schema(
                description = "Contraseña del usuario",
                example = "password123",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "La contraseña es obligatoria")
        String contrasena
) {
}
