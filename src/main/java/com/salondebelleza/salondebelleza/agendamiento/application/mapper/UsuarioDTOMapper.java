package com.salondebelleza.salondebelleza.agendamiento.application.mapper;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.usuario.ActualizarUsuarioRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.usuario.CrearUsuarioRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.usuario.UsuarioResponse;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Usuario;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

public interface UsuarioDTOMapper {

    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "activo", constant = "true")
    @Mapping(target = "mfaSecret", ignore = true)
    @Mapping(target = "codigosRecuperacion", ignore = true)
    @Mapping(target = "contrasena", ignore = true)
    Usuario toUsuario(CrearUsuarioRequest request);


    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "contrasena", ignore = true)
    @Mapping(target = "mfaSecret", ignore = true)
    @Mapping(target = "codigosRecuperacion", ignore = true)
    @Mapping(target = "mfaEnabled", ignore = true)
    @Mapping(target = "idEmpleado", ignore = true)
    void updateUsuarioFromDto(ActualizarUsuarioRequest request, @MappingTarget Usuario usuario);


    UsuarioResponse toUsuarioResponse(Usuario usuario);

}
