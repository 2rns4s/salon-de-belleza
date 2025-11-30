package com.salondebelleza.salondebelleza.agendamiento.application.dto.recursos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos necesarios para actualizar un recurso")
public record ActualizarRecursoRequest(

        @Schema(description = "Nombre del recurso", example = "Silla de corte")
        @Size(min = 3, max = 100, message = "El nombre debe tener una longitud de 3 a 100 caracteres.")
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @Schema(description = "Cantidad disponible", example = "3")
        @NotNull(message = "La cantidad es obligatoria")
        @Size(min = 0, message = "La cantidad debe ser mayor a 0")
        int cantidadTotal
) {
}
