package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.repository;


import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.RecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecursoRepository extends JpaRepository<RecursoEntity, Long> {
    Optional<RecursoEntity> findByNombreIgnoreCase (String nombre);
}
