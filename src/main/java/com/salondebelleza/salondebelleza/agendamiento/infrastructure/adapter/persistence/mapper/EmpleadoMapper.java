package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.mapper;

import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Calificacion;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Empleado;
import com.salondebelleza.salondebelleza.agendamiento.domain.valueobjects.FranjaLaboral;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.CalificacionEntity;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.EmpleadoEntity;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.FranjaLaboralEntity;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.ServicioEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EmpleadoMapper {

    @Mapping(target = "citas", ignore = true)
    Empleado toEmpleado(EmpleadoEntity empleadoEntity);


    EmpleadoEntity toEmpleadoEntity(Empleado empleado);

    @AfterMapping
    default void vincularRelaciones (@MappingTarget EmpleadoEntity empleadoEntity) {

        if (empleadoEntity.getFranjasLaborales() != null){
            empleadoEntity.getFranjasLaborales()
                    .forEach(franjaLaboral -> franjaLaboral.setEmpleadoEntity(empleadoEntity));
        }

        if (empleadoEntity.getCalificaciones() != null){
            empleadoEntity.getCalificaciones()
                    .forEach(calificacion -> calificacion.setEmpleadoEntity(empleadoEntity));
        }
    }

    @Mapping(target = "empleadoEntity", ignore = true)
    FranjaLaboralEntity toFranjaLaboralEntity(FranjaLaboral franjaLaboral);

    @Mapping(target = "idEmpleado", source = "empleadoEntity.idEmpleado" )
    FranjaLaboral toFranjaLaboral(FranjaLaboralEntity franjaLaboralEntity);



    @Mapping(target = "servicioEntity", source = "idServicio", qualifiedByName = "idToServicioEntity")
    @Mapping(target = "empleadoEntity", ignore = true)
    CalificacionEntity toCalificacionEntity(Calificacion calificacion);

    @Mapping(target = "idServicio", source = "servicioEntity.idServicio")
    @Mapping(target = "idEmpleado", source = "empleadoEntity.idEmpleado")
    Calificacion toCalificacion(CalificacionEntity calificacionEntity);


    @Named("idToServicioEntity")
    default ServicioEntity idToServicioEntity(Long idServicio) {
        if (idServicio == null) {
            return null;
        }

        ServicioEntity servicioEntity = new ServicioEntity();

        servicioEntity.setIdServicio(idServicio);

        return servicioEntity;
    }
}
