package com.salondebelleza.salondebelleza.agendamiento.application.dto.recursos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de respuesta para un recurso")
public record RecursoResponse(

        @Schema(description = "Identificador unico del recurso", example = "1")
        Long idRecurso,
        @Schema(description = "Nombre del recurso", example = "Silla de corte")
        String nombre,
        @Schema(description = "Cantidad total disponible", example = "3")
        int cantidadTotal
) {
}
