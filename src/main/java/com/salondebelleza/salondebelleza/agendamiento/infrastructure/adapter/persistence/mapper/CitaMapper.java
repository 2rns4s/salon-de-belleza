package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.mapper;

import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Cita;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.CitaRecursos;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.CitaEntity;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.CitaRecursosEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CitaMapper {

    // ----------------------------------------------------------------
    // 1. ENTITY -> DOMAIN (Leer de la BD)
    // ----------------------------------------------------------------

    @Mapping(source = "cliente.idCliente", target = "idCliente")
    @Mapping(source = "empleado.idEmpleado", target = "idEmpleado")
    @Mapping(source = "servicio.idServicio", target = "idServicio")
    @Mapping(source = "recursosReservados", target = "citaRecursos")
    Cita toCita (CitaEntity entity);

    @Mapping(source = "recursoEntity.idRecurso", target = "idRecurso")
    @Mapping(source = "citaEntity.idCita", target = "idCita")
    CitaRecursos toCitaRecurso(CitaRecursosEntity entity);


    // ----------------------------------------------------------------
    // 2. DOMAIN -> ENTITY (Guardar en la BD)
    // ----------------------------------------------------------------

    // Convertimos los IDs planos del dominio en los Objetos que JPA necesita
    @Mapping(source = "idCliente", target = "cliente.idCliente")
    @Mapping(source = "idEmpleado", target = "empleado.idEmpleado")
    @Mapping(source = "idServicio", target = "servicio.idServicio")
    @Mapping(source = "citaRecursos", target = "recursosReservados")
    CitaEntity toCitaEntity(Cita cita);

    @Mapping(source = "idRecurso", target = "recursoEntity.idRecurso")
    // El idCita se vinculará automáticamente cuando se guarde el padre, o con un @AfterMapping si fuera necesario
    @Mapping(target = "citaEntity", ignore = true)
    CitaRecursosEntity toCitaRecursoEntity(CitaRecursos domain);

    // Método auxiliar para listas (MapStruct lo genera, pero a veces es bueno declararlo)
    List<Cita> toDomainList(List<CitaEntity> entities);
}
