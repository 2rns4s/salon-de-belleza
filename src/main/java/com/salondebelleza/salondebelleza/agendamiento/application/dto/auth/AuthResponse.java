package com.salondebelleza.salondebelleza.agendamiento.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con token de acceso JWT tras completar el enrolamiento MFA")
public record AuthResponse(
        @Schema(
                description = "Token de acceso JWT válido para autenticar las solicitudes subsiguientes con el header Authorization: Bearer {token}",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        String token
) {

}
