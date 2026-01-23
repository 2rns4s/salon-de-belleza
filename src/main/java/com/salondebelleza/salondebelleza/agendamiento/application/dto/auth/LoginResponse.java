package com.salondebelleza.salondebelleza.agendamiento.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta del proceso de autenticación")
public record LoginResponse(
        @Schema(
                description = "Token JWT (puede ser token de acceso completo o token de pre-autenticación temporal)",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        String token,

        @Schema(
                description = "Indica si se requiere verificación MFA adicional. " +
                        "Si es true, el token es temporal y se debe llamar a /verify-mfa. " +
                        "Si es false, el token es completo y se puede usar directamente.",
                example = "false"
        )
        boolean mfaRequired,

        @Schema(
                description = "Mensaje informativo sobre el estado de la autenticación",
                example = "Inicio de sesión exitoso"
        )
        String mensaje
) {
}
