package com.salondebelleza.salondebelleza.agendamiento.domain.port;

import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepositoryPort {

    Cliente save(Cliente cliente);

    Optional<Cliente> findById(long idCliente);

    Optional<Cliente> findByTelefono(String telefono);

    Optional<Cliente> findByDocumento(String documento);

    List<Cliente> findByNombre(String nombre);

    List<Cliente> findAll();

    void deleteById(long idCliente);

}
