package com.salondebelleza.salondebelleza.agendamiento.application.dto.empleado;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Schema(
        description = "DTO de respuesta con la información de una franja laboral de un empleado",
        example = """
                {
                  "idFranjaLaboral": 1,
                  "dia": "MONDAY",
                  "horaInicio": "08:00:00",
                  "horaFin": "12:00:00"
                }
                """
)
public record FranjaLaboralResponse(

        @Schema(
                description = "Identificador único de la franja laboral en el sistema",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long idFranjaLaboral,

        @Schema(
                description = "Día de la semana de la franja laboral",
                example = "MONDAY",
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"}
        )
        DayOfWeek dia,

        @Schema(
                description = "Hora de inicio de la franja laboral en formato HH:mm:ss",
                example = "08:00:00",
                type = "string",
                format = "time",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        LocalTime horaInicio,

        @Schema(
                description = "Hora de finalización de la franja laboral en formato HH:mm:ss",
                example = "12:00:00",
                type = "string",
                format = "time",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        LocalTime horaFin
) {
}
