package com.salondebelleza.salondebelleza.agendamiento.application.mapper;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.cita.CitaRecursoResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.cita.CitaResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.cita.CrearCitaRequest;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Cita;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.CitaRecursos;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CitaDTOMapper {

    @Mapping(target = "idCita", ignore = true)
    @Mapping(target = "fechaFin", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "citaRecursos", ignore = true)
    Cita toCita(CrearCitaRequest request);

    @Mapping(target = "recursosAsignados", source = "citaRecursos")
    CitaResponse toCitaResponse(Cita cita);

    CitaRecursoResponse toCitaRecursoResponse(CitaRecursos citaRecursos);

    List<CitaResponse> toResponseList(List<Cita> citas);

}
