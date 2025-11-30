package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.mapper;

import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Recurso;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.RecursoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecursoMapper {

    RecursoEntity toRecursoEntity(Recurso recurso);

    @Mapping(target = "serviciosRecursos", ignore = true)
    @Mapping(target = "citaRecursos", ignore = true)
    Recurso toRecurso(RecursoEntity recursoEntity);

}
