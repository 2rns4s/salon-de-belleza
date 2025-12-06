package com.salondebelleza.salondebelleza.agendamiento.application.mapper;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.servicio.CrearServicioRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.servicio.RecursoEnServicioResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.servicio.ServicioResponse;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Servicio;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.ServicioRecursos;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServicioDTOMapper {

    @Mapping(target = "servicioRecursos", source = "recursos")
    @Mapping(target = "idServicio", ignore = true)
    Servicio toServicio (CrearServicioRequest request);

    @Mapping(target = "recursos", source = "servicioRecursos")
    ServicioResponse toServicioResponse(Servicio servicio);

    @Mapping(target = "nombre", source = "nombreRecurso")
    RecursoEnServicioResponse toRecursoEnServicioResponse(ServicioRecursos servicioRecursos);
}
