package com.salondebelleza.salondebelleza.agendamiento.application.usecase;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.ClienteResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.ClienteDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.ClienteRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuscarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ClienteDTOMapper clienteDTOMapper;

    @Autowired
    public BuscarClienteUseCase(ClienteRepositoryPort clienteRepositoryPort, ClienteDTOMapper clienteDTOMapper) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.clienteDTOMapper = clienteDTOMapper;
    }

    public ClienteResponse executeById(long idCliente){
        return clienteDTOMapper.toClienteResponse(clienteRepositoryPort.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado")));
    }

    public ClienteResponse executeByDocumento(String documento){
        return clienteDTOMapper.toClienteResponse(clienteRepositoryPort.findByDocumento(documento)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado")));
    }

    public ClienteResponse executeByTelefono(String telefono){
        return clienteDTOMapper.toClienteResponse(clienteRepositoryPort.findByTelefono(telefono)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado")));
    }
}
