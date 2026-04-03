package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.mapper;

import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Usuario;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.EmpleadoEntity;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(source = "empleadoEntity.idEmpleado", target = "idEmpleado")
    @Mapping(source = "mfaEnabled", target = "mfaEnabled")
    Usuario toUsuario(UsuarioEntity usuarioEntity);

    @Mapping(source = "idEmpleado", target = "empleadoEntity", qualifiedByName = "idToEmpleadoEntity")
    @Mapping(source = "mfaEnabled", target = "mfaEnabled")
    UsuarioEntity toUsuarioEntity(Usuario usuario);

    @Named("idToEmpleadoEntity")
    default EmpleadoEntity idToEmpleadoEntity(Long idEmpleado) {
        if (idEmpleado == null) return null;
        EmpleadoEntity emp = new EmpleadoEntity();
        emp.setIdEmpleado(idEmpleado);
        return emp;
    }

}
