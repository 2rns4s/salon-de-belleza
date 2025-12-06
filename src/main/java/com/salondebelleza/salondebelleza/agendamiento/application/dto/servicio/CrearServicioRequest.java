package com.salondebelleza.salondebelleza.agendamiento.application.dto.servicio;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;


public record CrearServicioRequest (

        @NotBlank(message = "El nombre es obligatorio")
        @Schema(description = "Nombre del servicio", example = "Corte")
        @Size(min = 3, max = 100, message = "El nombre debe tener una longitud de 3 a 100 caracteres.")
        String nombre,

        @NotNull(message = "La duración es obligatoria")
        @Min(value = 1, message = "La duración debe ser mayor a 0")
        @Schema(description = "Duración del servicio en minutos", example = "30")
        int duracion,

        @NotNull(message = "Los recursos son obligatorios")
        @Size(min = 1, message = "Debe haber al menos un recurso")
        @Schema(description = "Recursos requeridos para el servicio")
        List<RecursoRequerido> recursos
){
}
