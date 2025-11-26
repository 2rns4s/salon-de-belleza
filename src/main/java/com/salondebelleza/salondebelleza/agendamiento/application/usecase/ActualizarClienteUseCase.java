package com.salondebelleza.salondebelleza.agendamiento.application.usecase;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.ActualizarClienteRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.ClienteResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.ClienteDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Cliente;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.ClienteExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.ClienteNoEncontradoException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.ClienteRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ActualizarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ClienteDTOMapper clienteDTOMapper;

    @Autowired
    public ActualizarClienteUseCase(ClienteRepositoryPort clienteRepositoryPort, ClienteDTOMapper clienteDTOMapper) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.clienteDTOMapper = clienteDTOMapper;
    }

    public ClienteResponse execute(Long idCliente, ActualizarClienteRequest request) {
        Cliente cliente = this.clienteRepositoryPort.findById(idCliente).orElseThrow(
                () -> new ClienteNoEncontradoException("Cliente con id " + idCliente + " no encontrado")
        );

        Optional<Cliente> clienteExistente = this.clienteRepositoryPort.findByTelefono(request.telefono());

        if (clienteExistente.isPresent()){
            if (!idCliente.equals(clienteExistente.get().getIdCliente())){
                throw new ClienteExistenteException("El telefono " + request.telefono() + " ya existe");
            }
        }

        cliente.setNombre(request.nombre());
        cliente.setApellido(request.apellido());
        cliente.setTelefono(request.telefono());


        return this.clienteDTOMapper.toClienteResponse(this.clienteRepositoryPort.save(cliente));
    }

}
