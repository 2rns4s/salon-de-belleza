package com.salondebelleza.salondebelleza.agendamiento.application.dto.cita;

import com.salondebelleza.salondebelleza.agendamiento.domain.entities.EstadoCita;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        description = "DTO para actualizar el estado de una cita existente. Permite cambiar el estado de la cita entre PENDIENTE, CONFIRMADA, CANCELADA o REALIZADA.",
        example = """
                {
                  "estado": "CONFIRMADA"
                }
                """
)
public record ActualizarCitaRequest(
        @NotNull(message = "El nuevo estado es obligatorio")
        @Schema(
                description = "Nuevo estado de la cita. Valores posibles: PENDIENTE (cita programada pero no confirmada), CONFIRMADA (cliente confirmó asistencia), CANCELADA (cita fue cancelada), REALIZADA (servicio fue completado)",
                example = "CONFIRMADA",
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"PENDIENTE", "CONFIRMADA", "CANCELADA", "REALIZADA"}
        )
        EstadoCita estado
) {
}
