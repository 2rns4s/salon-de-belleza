package com.salondebelleza.salondebelleza.agendamiento.application.usecase;

import com.salondebelleza.salondebelleza.agendamiento.domain.exception.ClienteNoEncontradoException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.ClienteRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EliminarClienteUseCase {
    private final ClienteRepositoryPort clienteRepositoryPort;

    @Autowired
    public EliminarClienteUseCase(ClienteRepositoryPort clienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    public void executeById(long idCliente){
        this.clienteRepositoryPort.findById(idCliente).orElseThrow(
                () -> new ClienteNoEncontradoException("Cliente con id " + idCliente + " no encontrado")
        );
        clienteRepositoryPort.deleteById(idCliente);
    }
}
