package com.salondebelleza.salondebelleza.agendamiento.application.dto.reporte;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Métricas de rendimiento de citas dentro del rango consultado")
public record RendimientoDTO(
        @Schema(description = "Total de citas registradas en el rango", example = "42")
        long totalCitas,
        @Schema(description = "Cantidad de citas con estado REALIZADA", example = "30")
        long completadas,
        @Schema(description = "Cantidad de citas con estado CANCELADA", example = "8")
        long canceladas,
        @Schema(description = "Porcentaje de completitud sobre el total de citas", example = "71.43")
        double tasaCompletitud
) {
}
