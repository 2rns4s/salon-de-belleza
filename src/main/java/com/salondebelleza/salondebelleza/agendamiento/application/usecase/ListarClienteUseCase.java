package com.salondebelleza.salondebelleza.agendamiento.application.usecase;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.cliente.ClienteResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.ClienteDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.ClienteRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ClienteDTOMapper clienteDTOMapper;

    @Autowired
    public ListarClienteUseCase(ClienteRepositoryPort clienteRepositoryPort, ClienteDTOMapper clienteDTOMapper) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.clienteDTOMapper = clienteDTOMapper;
    }

    public List<ClienteResponse> executeByAll(){
        return this.clienteRepositoryPort.findAll()
                .stream()
                .map(clienteDTOMapper::toClienteResponse)
                .toList();
    }

    public List<ClienteResponse> executeByNombre(String nombre){
        return this.clienteRepositoryPort.findByNombre(nombre)
                .stream()
                .map(clienteDTOMapper::toClienteResponse)
                .toList();
    }
}
