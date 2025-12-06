package com.salondebelleza.salondebelleza.agendamiento.application.dto.servicio;

import java.util.List;

public record ServicioResponse (
        Long idServicio,
        String nombre,
        int duracion,
        List<RecursoEnServicioResponse> recursos
){
}
