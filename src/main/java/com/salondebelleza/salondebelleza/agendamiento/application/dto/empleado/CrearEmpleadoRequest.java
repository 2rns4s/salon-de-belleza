package com.salondebelleza.salondebelleza.agendamiento.application.dto.empleado;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

@Schema(
        description = "DTO para la creación de un nuevo empleado en el sistema",
        example = """
                {
                  "nombre": "Juan",
                  "apellido": "Pérez",
                  "correo": "juan.perez@salondebelleza.com",
                  "telefono": "3001234567",
                  "franjasLaborales": [
                    {
                      "dia": "MONDAY",
                      "horaInicio": "08:00:00",
                      "horaFin": "12:00:00"
                    },
                    {
                      "dia": "MONDAY",
                      "horaInicio": "14:00:00",
                      "horaFin": "18:00:00"
                    }
                  ],
                  "serviciosIds": [1, 2, 3]
                }
                """
)
public record CrearEmpleadoRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Schema(
                description = "Nombre del empleado",
                example = "Juan",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 1,
                maxLength = 100
        )
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Schema(
                description = "Apellido del empleado",
                example = "Pérez",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 1,
                maxLength = 100
        )
        String apellido,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo debe tener un formato válido")
        @Schema(
                description = "Correo electrónico del empleado. Debe ser único en el sistema.",
                example = "juan.perez@salondebelleza.com",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "email"
        )
        String correo,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(
                regexp = "^3\\d{9}$",
                message = "El celular debe tener 10 dígitos y empezar por 3 (formato nacional colombiano, sin +57).")
        @Schema(
                description = "Número de teléfono celular del empleado. Formato colombiano: 10 dígitos iniciando con 3 (sin prefijo +57).",
                example = "3001234567",
                requiredMode = Schema.RequiredMode.REQUIRED,
                pattern = "^3\\d{9}$"
        )
        String telefono,

        @NotEmpty(message = "Debe definir al menos una franja laboral")
        @Valid
        @Schema(
                description = "Lista de franjas horarias laborales del empleado. Define cuándo está disponible para trabajar.",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 1,
                implementation = CrearFranjaRequest.class
        )
        List<CrearFranjaRequest> franjasLaborales,

        @NotEmpty(message = "Debe asignar al menos un servicio al empleado")
        @Schema(
                description = "Lista de identificadores de los servicios que el empleado está capacitado para realizar",
                example = "[1, 2, 3]",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 1
        )
        List<Long> serviciosIds
) {
}
