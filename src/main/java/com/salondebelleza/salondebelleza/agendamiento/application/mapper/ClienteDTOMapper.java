package com.salondebelleza.salondebelleza.agendamiento.application.mapper;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.cliente.ClienteResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.cliente.CrearClienteRequest;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClienteDTOMapper {

    ClienteResponse toClienteResponse(Cliente cliente);

    @Mapping(target = "idCliente", ignore = true)
    @Mapping(target = "citas", ignore = true)
    Cliente toCliente(CrearClienteRequest request);

}
