package com.salondebelleza.salondebelleza.agendamiento.application.dto.cita;

import com.salondebelleza.salondebelleza.agendamiento.domain.entities.EstadoCita;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record CitaResponse(
        @Schema(description = "ID único de la cita", example = "100")
        Long idCita,

        @Schema(description = "ID del cliente", example = "1")
        Long idCliente,

        @Schema(description = "ID del servicio", example = "5")
        Long idServicio,

        @Schema(description = "ID del empleado encargado", example = "2")
        Long idEmpleado,

        @Schema(description = "Fecha y hora de inicio", example = "2025-12-25T10:00:00")
        LocalDateTime fechaInicio,

        @Schema(description = "Fecha y hora de fin (Calculada según duración del servicio)", example = "2025-12-25T11:00:00")
        LocalDateTime fechaFin,

        @Schema(description = "Estado actual de la cita", example = "PENDIENTE")
        EstadoCita estado,

        @Schema(description = "Lista de recursos físicos que el sistema reservó para esta cita")
        List<CitaRecursoResponse> recursosAsignados
) {
}
