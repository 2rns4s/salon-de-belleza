package com.salondebelleza.salondebelleza.agendamiento.application.dto.servicio;

public record RecursoEnServicioResponse(
        Long idRecurso,
        String nombre,
        int cantidadRequerida
) {
}
