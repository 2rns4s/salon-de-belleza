package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence;


import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Cliente;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.ClienteRepositoryPort;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.ClienteEntity;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.mapper.ClienteMapper;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ClientePersistenceAdapter implements ClienteRepositoryPort {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Autowired
    public ClientePersistenceAdapter(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    @Override
    public Cliente save(Cliente cliente) {
        return clienteMapper
                .toCliente(clienteRepository
                        .save(clienteMapper
                                .toClienteEntity(cliente)));
    }

    @Override
    public Optional<Cliente> findById(long idCliente) {
        return clienteRepository.findById(idCliente)
                .map(clienteMapper::toCliente);
    }

    @Override
    public Optional<Cliente> findByTelefono(String telefono) {
        return clienteRepository.findByTelefono(telefono)
                .map(clienteMapper::toCliente);
    }

    @Override
    public Optional<Cliente> findByDocumento(String documento) {
        return clienteRepository.findByDocumento(documento)
                .map(clienteMapper::toCliente);
    }

    @Override
    public List<Cliente> findByNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(clienteMapper::toCliente)
                .toList();
    }

    @Override
    public List<Cliente> findAll() {
        return clienteRepository.findAll()
                .stream().map(clienteMapper::toCliente)
                .toList();
    }

    @Override
    public void deleteById(long idCliente) {
        clienteRepository.deleteById(idCliente);
    }
}
