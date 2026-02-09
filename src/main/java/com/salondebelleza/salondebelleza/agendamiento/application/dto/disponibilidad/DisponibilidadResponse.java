package com.salondebelleza.salondebelleza.agendamiento.application.dto.disponibilidad;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Schema(description = "Response que contiene la disponibilidad de horarios para una fecha específica")
public record DisponibilidadResponse(

        @Schema(
                description = "Fecha consultada para la disponibilidad de horarios",
                example = "2026-02-15",
                type = "string",
                format = "date"
        )
        LocalDate fecha,

        @Schema(
                description = "Lista de horarios disponibles para la fecha consultada. " +
                        "Cada horario representa una hora de inicio posible para la cita. " +
                        "La lista estará vacía si no hay horarios disponibles. " +
                        "Los horarios se retornan en formato HH:mm:ss (24 horas).",
                example = "[\"09:00:00\", \"10:00:00\", \"11:00:00\", \"14:00:00\", \"15:00:00\"]"
        )
        List<LocalTime> horariosDisponibles
) {
}
