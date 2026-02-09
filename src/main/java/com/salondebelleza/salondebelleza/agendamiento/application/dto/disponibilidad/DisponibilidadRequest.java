package com.salondebelleza.salondebelleza.agendamiento.application.dto.disponibilidad;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Schema(description = "Request para consultar la disponibilidad de horarios")
public record DisponibilidadRequest(

        @Schema(
                description = "Fecha para la cual se desea consultar la disponibilidad de horarios. " +
                        "Debe ser la fecha actual o una fecha futura. El formato debe ser YYYY-MM-DD (ISO 8601).",
                example = "2026-02-15",
                required = true,
                type = "string",
                format = "date"
        )
        @NotNull(message = "La fecha es obligatoria")
        @FutureOrPresent(message = "La fecha no puede ser en el pasado")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate fecha,

        @Schema(
                description = "Identificador único del servicio para el cual se desea consultar disponibilidad. " +
                        "Este parámetro es necesario para calcular correctamente la duración de la cita " +
                        "y verificar la disponibilidad de recursos específicos del servicio.",
                example = "1",
                required = true,
                minimum = "1"
        )
        @NotNull(message = "El id del servicio es obligatorio")
        @Positive(message = "El ID del servicio debe ser positivo")
        Long idServicio,

        @Schema(
                description = "Identificador único del empleado (opcional). Si se proporciona, " +
                        "la consulta filtrará la disponibilidad solo para ese empleado específico. " +
                        "Si se omite, se retornará la disponibilidad agregada de todos los empleados capacitados para el servicio.",
                example = "3",
                required = false,
                minimum = "1"
        )
        @Positive(message = "El id del empleado debe ser mayor a 0")
        Long idEmpleado
) {
}
