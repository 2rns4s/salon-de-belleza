package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.reporte.DashboardResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.usecase.reporte.GenerarDashboardUseCase;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("${api.base-path}/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "API de reportes operativos para propietarios del salón")
public class DashboardController {

    private final GenerarDashboardUseCase generarDashboardUseCase;

    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    @Operation(
            summary = "Obtener dashboard por rango de fechas",
            description = "Genera métricas operativas y top de servicios realizados en un rango de fechas. " +
                    "Disponible únicamente para usuarios con rol OWNER."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Dashboard generado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DashboardResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado por permisos insuficientes",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno al generar el dashboard",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<DashboardResponse> obtenerDashboard(
            @Parameter(
                    description = "Inicio del rango en formato ISO-8601 (yyyy-MM-dd'T'HH:mm:ss)",
                    required = true,
                    example = "2026-04-01T00:00:00"
            )
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(
                    description = "Fin del rango en formato ISO-8601 (yyyy-MM-dd'T'HH:mm:ss)",
                    required = true,
                    example = "2026-04-30T23:59:59"
            )
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin
            ) {
        return ResponseEntity.status(HttpStatus.OK).body(generarDashboardUseCase.execute(fechaInicio, fechaFin));
    }
}
