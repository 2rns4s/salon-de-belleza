package com.salondebelleza.salondebelleza.agendamiento.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Datos para confirmar el enrolamiento MFA del usuario autenticado")
public record MfaSetupRequest(
        @Schema(
                description = "Secreto TOTP recibido en la respuesta del endpoint `/setup`",
                example = "JBSWY3DPEHPK3PXP",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El secreto MFA no puede estar vacío")
        String secret,

        @Schema(
                description = "Código TOTP de 6 dígitos generado por la aplicación de autenticación (Google Authenticator, Authy, etc.)",
                example = "123456",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El código MFA no puede estar vacío")
        @Pattern(regexp = "\\d{6}", message = "El código MFA debe ser un número de 6 dígitos")
        String code
){
        }
