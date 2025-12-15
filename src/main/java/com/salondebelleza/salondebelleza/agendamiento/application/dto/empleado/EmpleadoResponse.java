package com.salondebelleza.salondebelleza.agendamiento.application.dto.empleado;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(
        description = "DTO de respuesta con la información completa de un empleado",
        example = """
                {
                  "idEmpleado": 1,
                  "nombre": "Juan",
                  "apellido": "Pérez",
                  "correo": "juan.perez@salon.com",
                  "telefono": "3001234567",
                  "franjasLaborales": [
                    {
                      "idFranjaLaboral": 1,
                      "dia": "MONDAY",
                      "horaInicio": "08:00:00",
                      "horaFin": "12:00:00"
                    }
                  ],
                  "serviciosIds": [1, 2, 5]
                }
                """
)
public record EmpleadoResponse(

        @Schema(
                description = "Identificador único del empleado en el sistema",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long idEmpleado,

        @Schema(
                description = "Nombre del empleado",
                example = "Juan",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String nombre,

        @Schema(
                description = "Apellido del empleado",
                example = "Pérez",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String apellido,

        @Schema(
                description = "Correo electrónico del empleado",
                example = "juan.perez@salon.com",
                format = "email",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String correo,

        @Schema(
                description = "Número de teléfono celular del empleado (formato colombiano)",
                example = "3001234567",
                pattern = "^3\\d{9}$",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String telefono,

        @Schema(
                description = "Lista de franjas horarias laborales asignadas al empleado",
                requiredMode = Schema.RequiredMode.REQUIRED,
                implementation = FranjaLaboralResponse.class
        )
        List<FranjaLaboralResponse> franjasLaborales,

        @Schema(
                description = "Lista de identificadores de los servicios que el empleado puede realizar",
                example = "[1, 2, 5]",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        List<Long> serviciosIds
) {
}
