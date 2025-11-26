package com.salondebelleza.salondebelleza.agendamiento.application.dto.cliente;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de respuesta para un cliente")
public record ClienteResponse(

        @Schema(description = "Identificador unico del cliente", example = "1")
        Long idCliente,

        @Schema(description = "Nombre del cliente", example = "Juan")
        String nombre,

        @Schema(description = "Apellido del cliente", example = "Perez")
        String apellido,

        @Schema(description = "Documento del cliente", example = "1234567890")
        String documento,

        @Schema(description = "Telefono del cliente", example = "3000000000")
        String telefono
) {
}
