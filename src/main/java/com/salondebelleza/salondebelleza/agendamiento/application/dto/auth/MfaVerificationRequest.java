package com.salondebelleza.salondebelleza.agendamiento.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Datos necesarios para verificar el código MFA (Multi-Factor Authentication)")
public record MfaVerificationRequest(

        @Schema(
                description = "Nombre de usuario único del usuario que está verificando MFA",
                example = "admin",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El nombre de usuario es obligatorio")
        String nombreUsuario,

        @Schema(
                description = "Código TOTP de 6 dígitos generado por la aplicación de autenticación " +
                        "(Google Authenticator, Microsoft Authenticator, Authy, etc.)",
                example = "123456",
                pattern = "^\\d{6}$",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El código TOTP es obligatorio")
        @Pattern(regexp = "^\\d{6}$", message = "El código debe contener exactamente 6 dígitos numéricos")
        String totpCode,

        @Schema(
                description = "Token de pre-autenticación temporal recibido del endpoint /login cuando mfaRequired es true",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El token de pre-autenticación es obligatorio")
        String preAuthToken
) {
}
