package com.salondebelleza.salondebelleza.agendamiento.domain.port;

import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Recurso;

import java.util.Optional;

public interface RecursoRepositoryPort {

    Recurso save(Recurso recurso);

    Optional<Recurso> findById(long idRecurso);

    Optional<Recurso> findByNombre(String nombre);

    void delete(Long idRecurso);

}
