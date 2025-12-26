package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.repository;


import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.RecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecursoRepository extends JpaRepository<RecursoEntity, Long> {

    List<RecursoEntity> findByNombreIgnoreCase (String nombre);

    Optional<RecursoEntity> findFirstByNombreContainsIgnoreCase (String nombre);
}
