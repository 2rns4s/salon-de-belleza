package com.salondebelleza.salondebelleza.agendamiento.application.dto.reporte;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Respuesta consolidada del dashboard operativo")
public record DashboardResponse(
        @Schema(description = "Resumen de rendimiento de citas en el rango consultado")
        RendimientoDTO rendimiento,
        @Schema(description = "Top de servicios realizados en el rango consultado")
        List<ServicioTopDTO> serviciosTop
) {
}
