package com.salondebelleza.salondebelleza.agendamiento.application.dto.cita;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con los datos de la asignación de recurso físico a una cita")
public record CitaRecursoResponse(
        @Schema(description = "ID de la asignación", example = "10")
        Long idCitaRecurso,

        @Schema(description = "ID del recurso físico asignado (Ej: Silla #1)", example = "3")
        Long idRecurso
) {
}
