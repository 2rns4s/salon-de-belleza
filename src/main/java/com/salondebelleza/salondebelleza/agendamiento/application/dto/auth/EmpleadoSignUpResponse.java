package com.salondebelleza.salondebelleza.agendamiento.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de la cuenta creada tras el registro exitoso del empleado")
public record EmpleadoSignUpResponse(
        @Schema(
                description = "ID del empleado al que pertenece la cuenta recién creada",
                example = "5"
        )
        Long idEmpleado,

        @Schema(
                description = "Nombre de usuario asignado a la cuenta",
                example = "maria_lopez"
        )
        String nombreUsuario,

        @Schema(
                description = "Rol asignado automáticamente a la cuenta. Siempre será RECEPTIONIST para el auto-registro.",
                example = "RECEPTIONIST"
        )
        String rol
) {
}
