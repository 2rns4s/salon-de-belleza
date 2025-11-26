package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.repository;

import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {

    Optional<ClienteEntity> findByDocumento(String documento);

    Optional<ClienteEntity> findByTelefono(String telefono);

    List<ClienteEntity> findByNombreContainingIgnoreCase(String nombre);

}
