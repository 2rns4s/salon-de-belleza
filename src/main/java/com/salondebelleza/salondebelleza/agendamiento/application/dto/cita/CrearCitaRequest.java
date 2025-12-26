package com.salondebelleza.salondebelleza.agendamiento.application.dto.cita;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(
        description = "DTO para la creación de una nueva cita en el sistema",
        example = """
                {
                  "idCliente": 1,
                  "idServicio": 5,
                  "idEmpleado": 2,
                  "fechaInicio": "2025-12-26T10:00:00"
                }
                """
)
public record CrearCitaRequest(
        @NotNull(message = "El cliente es obligatorio")
        @Schema(
                description = "ID único del cliente que solicita la cita. Debe corresponder a un cliente existente en el sistema.",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long idCliente,

        @NotNull(message = "El servicio es obligatorio")
        @Schema(
                description = "ID único del servicio a realizar en la cita. Debe corresponder a un servicio existente en el sistema.",
                example = "5",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long idServicio,

        @NotNull(message = "El empleado es obligatorio")
        @Schema(
                description = "ID único del empleado que realizará el servicio. El empleado debe estar asignado al servicio especificado y disponible en la fecha/hora solicitada.",
                example = "2",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long idEmpleado,

        @NotNull(message = "La fecha de inicio es obligatoria")
        @Future(message = "La cita debe ser en el futuro")
        @Schema(
                description = "Fecha y hora de inicio de la cita. Debe ser una fecha futura y dentro del horario laboral del empleado. La hora de fin se calcula automáticamente según la duración del servicio.",
                example = "2025-12-26T10:00:00",
                requiredMode = Schema.RequiredMode.REQUIRED,
                type = "string",
                format = "date-time"
        )
        LocalDateTime fechaInicio
) {
}
