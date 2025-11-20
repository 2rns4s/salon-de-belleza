package com.salondebelleza.salondebelleza.agendamiento.application.dto;

public record ClienteResponse(
        Long idCliente,
        String nombre,
        String apellido,
        String documento,
        String telefono
) {
}
