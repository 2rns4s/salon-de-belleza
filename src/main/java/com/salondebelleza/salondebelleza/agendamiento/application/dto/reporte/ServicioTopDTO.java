package com.salondebelleza.salondebelleza.agendamiento.application.dto.reporte;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Servicio con mayor cantidad de ejecuciones en el rango")
public record ServicioTopDTO(
        @Schema(description = "Nombre del servicio", example = "Corte de cabello")
        String nombreServicio,
        @Schema(description = "Cantidad de veces que se realizó el servicio", example = "15")
        long cantidadRealizada
) {
}
