package com.salondebelleza.salondebelleza.agendamiento.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de configuración inicial para enrolar MFA en la aplicación de autenticación")
public record MfaSetupResponse(
        @Schema(
                description = "Secreto TOTP en formato Base32 que se debe guardar y enviar en el siguiente paso a `/enable`",
                example = "JBSWY3DPEHPK3PXP"
        )
        String secret,

        @Schema(
                description = "URL de datos de imagen QR en formato Base64 (data:image/png;base64,...) lista para renderizar en el frontend",
                example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
        )
        String qrCodeUrl
) {
}
