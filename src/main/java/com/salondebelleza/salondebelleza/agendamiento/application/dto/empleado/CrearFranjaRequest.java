package com.salondebelleza.salondebelleza.agendamiento.application.dto.empleado;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Schema(
        description = "DTO para definir una franja horaria laboral de un empleado",
        example = """
                {
                  "dia": "MONDAY",
                  "horaInicio": "08:00:00",
                  "horaFin": "12:00:00"
                }
                """
)
public record CrearFranjaRequest(

        @NotNull(message = "El día es obligatorio")
        @Schema(
                description = "Día de la semana para la franja laboral. Valores permitidos: MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY",
                example = "MONDAY",
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"}
        )
        DayOfWeek dia,

        @NotNull(message = "La hora de inicio es obligatoria")
        @Schema(
                description = "Hora de inicio de la franja laboral en formato HH:mm:ss",
                example = "08:00:00",
                type = "string",
                format = "time",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        LocalTime horaInicio,

        @NotNull(message = "La hora de fin es obligatoria")
        @Schema(
                description = "Hora de finalización de la franja laboral en formato HH:mm:ss. Debe ser posterior a horaInicio.",
                example = "12:00:00",
                type = "string",
                format = "time",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        LocalTime horaFin
) {
}
