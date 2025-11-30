package com.salondebelleza.salondebelleza.agendamiento.application.mapper;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.recursos.ActualizarRecursoRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.recursos.CrearRecursoRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.recursos.RecursoResponse;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Recurso;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecursoDTOMapper {

    RecursoResponse toRecursoResponse(Recurso recurso);

    @Mapping(target = "serviciosRecursos", ignore = true)
    @Mapping(target = "citaRecursos", ignore = true)
    @Mapping(target = "idRecurso", ignore = true)
    Recurso toRecurso(CrearRecursoRequest request);

    @Mapping(target = "serviciosRecursos", ignore = true)
    @Mapping(target = "citaRecursos", ignore = true)
    @Mapping(target = "idRecurso", ignore = true)
    Recurso toRecurso(ActualizarRecursoRequest request);
}
