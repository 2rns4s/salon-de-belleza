package com.salondebelleza.salondebelleza.agendamiento.application.usecase;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.cliente.ClienteResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.cliente.CrearClienteRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.ClienteDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.ClienteExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.ClienteRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CrearClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ClienteDTOMapper clienteDTOMapper;

    @Autowired
    public CrearClienteUseCase(ClienteRepositoryPort clienteRepositoryPort, ClienteDTOMapper clienteDTOMapper) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.clienteDTOMapper = clienteDTOMapper;
    }

    public ClienteResponse execute(CrearClienteRequest request){
        this.clienteRepositoryPort.findByTelefono(request.telefono()).ifPresent(
                e -> {throw new ClienteExistenteException("El telefono " + request.telefono() + " ya existe");}
        );

        this.clienteRepositoryPort.findByDocumento(request.documento()).ifPresent(
                e -> {throw new ClienteExistenteException("El documento " + request.documento() + " ya existe");}
        );

        return this.clienteDTOMapper
                .toClienteResponse(this.clienteRepositoryPort
                        .save(this.clienteDTOMapper
                                .toCliente(request)));
    }
}
