package com.salondebelleza.salondebelleza.agendamiento.domain.port;

import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Recurso;

import java.util.List;
import java.util.Optional;

public interface RecursoRepositoryPort {

    List<Recurso> findAll();

    Recurso save(Recurso recurso);

    Optional<Recurso> findById(long idRecurso);

    List<Recurso> findByNombreIgnoreCase(String nombre);

    Optional<Recurso> findOneByNombreIgnoreCase (String nombre);

    void delete(Long idRecurso);

}
