package com.salondebelleza.salondebelleza.agendamiento.application.mapper;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.empleado.*;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Calificacion;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Empleado;
import com.salondebelleza.salondebelleza.agendamiento.domain.valueobjects.FranjaLaboral;
import org.mapstruct.*;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EmpleadoDTOMapper {

    @Mapping(target = "idEmpleado", ignore = true)
    @Mapping(target = "citas", ignore = true)
    @Mapping(target = "franjasLaborales", source = "franjasLaborales")
    @Mapping(target = "calificaciones", source = "serviciosIds", qualifiedByName = "idsToCalificaciones")
    Empleado toEmpleado (CrearEmpleadoRequest request);

    @Mapping(target = "idFranjaLaboral", ignore = true)
    @Mapping(target = "idEmpleado", ignore = true)
    FranjaLaboral toFranjaLaboral(CrearFranjaRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idEmpleado", ignore = true)
    @Mapping(target = "citas", ignore = true)
    @Mapping(target = "franjasLaborales", ignore = true)
    @Mapping(target = "calificaciones", ignore = true)
    void updateEmpleadoFromRequest(ActualizarEmpleadoRequest request, @MappingTarget Empleado domain);

    @Mapping(target = "serviciosIds", source = "calificaciones", qualifiedByName = "calificacionesToIds")
    EmpleadoResponse toEmpleadoResponse(Empleado empleado);

    FranjaLaboralResponse toFranjaResponse(FranjaLaboral franja);



    @Named("idsToCalificaciones")
    default List<Calificacion> idsToCalificaciones(List<Long> serviciosIds) {
        if (serviciosIds == null) return Collections.emptyList();

        return serviciosIds.stream()
                .map(idServicio -> {
                    Calificacion cal = new Calificacion();
                    cal.setIdServicio(idServicio);
                    // El idEmpleado se vinculará más adelante en la persistencia
                    return cal;
                })
                .toList();
    }

    @Named("calificacionesToIds")
    default List<Long> calificacionesToIds(List<Calificacion> calificaciones) {
        if (calificaciones == null) return Collections.emptyList();

        return calificaciones.stream()
                .map(Calificacion::getIdServicio)
                .toList();
    }
}
