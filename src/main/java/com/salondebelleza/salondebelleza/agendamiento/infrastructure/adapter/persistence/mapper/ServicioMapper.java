package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.mapper;

import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Servicio;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.ServicioRecursos;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.ServicioEntity;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.ServicioRecursosEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServicioMapper {

    @Mapping(source = "recursoEntity.idRecurso", target = "idRecurso")
    ServicioRecursos toServicioRecursos(ServicioRecursosEntity servicioRecursosEntity);

    @Mapping(ignore = true, target = "calificaciones")
    @Mapping(ignore = true, target = "citas")
    Servicio toServicio(ServicioEntity servicioEntity);


    ServicioEntity toServicioEntity(Servicio servicio);

    @Mapping(target = "idServicioRecursos", ignore = true)
    @Mapping(target = "servicioEntity", ignore = true)
    @Mapping(target = "recursoEntity", ignore = true)
    ServicioRecursosEntity toServicioRecursosEntity(ServicioRecursos servicioRecursos);
}
