package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Formato estándar de error para respuestas no exitosas")
public record ErrorResponse(
        @Schema(description = "Fecha y hora del error", example = "2026-04-10T16:30:00")
        LocalDateTime timestamp,
        @Schema(description = "Código HTTP", example = "400")
        int status,
        @Schema(description = "Nombre del estado HTTP", example = "Bad Request")
        String error,
        @Schema(description = "Mensaje principal del error", example = "Error en los datos enviados")
        String message,
        @Schema(description = "Detalles adicionales del error, normalmente para validaciones")
        List<String> details
) {
}
