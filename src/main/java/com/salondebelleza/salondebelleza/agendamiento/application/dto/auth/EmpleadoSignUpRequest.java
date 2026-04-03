package com.salondebelleza.salondebelleza.agendamiento.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para que un empleado pre-registrado cree su cuenta de usuario")
public record EmpleadoSignUpRequest(
        @Schema(
                description = "Correo electrónico del empleado registrado previamente por el administrador",
                example = "maria.lopez@salon.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Formato de correo inválido")
        String email,

        @Schema(
                description = "Nombre de usuario único para la cuenta. Debe tener entre 4 y 20 caracteres.",
                example = "maria_lopez",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 4, max = 20, message = "El username debe tener entre 4 y 20 caracteres")
        String username,

        @Schema(
                description = "Contraseña para la cuenta. Debe tener mínimo 8 caracteres.",
                example = "MiPassword123",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres")
        String password
) {
}
