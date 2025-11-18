package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.mapper;

import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Cliente;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.ClienteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteEntity toClienteEntity(Cliente cliente);

    @Mapping(target = "citas", ignore = true)
    Cliente toCliente(ClienteEntity clienteEntity);
}
