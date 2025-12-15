package com.salondebelleza.salondebelleza.agendamiento.application.dto.empleado;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(
        description = "DTO para actualizar la información básica de un empleado existente",
        example = """
                {
                  "nombre": "Juan David",
                  "apellido": "Pérez Gomez",
                  "correo": "juan.nuevo@salon.com",
                  "telefono": "3009998877"
                }
                """
)
public record ActualizarEmpleadoRequest(
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        @Schema(
                description = "Nuevo nombre del empleado. Campo opcional para actualización parcial.",
                example = "Juan David",
                nullable = true,
                minLength = 2,
                maxLength = 100
        )
        String nombre,

        @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
        @Schema(
                description = "Nuevo apellido del empleado. Campo opcional para actualización parcial.",
                example = "Pérez Gomez",
                nullable = true,
                minLength = 2,
                maxLength = 100
        )
        String apellido,

        @Email(message = "El formato del correo no es válido")
        @Schema(
                description = "Nuevo correo electrónico del empleado. Debe ser único en el sistema.",
                example = "juan.nuevo@salon.com",
                format = "email",
                nullable = true
        )
        String correo,

        @Pattern(regexp = "^3\\d{9}$", message = "El celular debe tener 10 dígitos y empezar por 3")
        @Schema(
                description = "Nuevo número de teléfono celular del empleado. Formato colombiano: 10 dígitos iniciando con 3.",
                example = "3009998877",
                pattern = "^3\\d{9}$",
                nullable = true
        )
        String telefono
) {
}
