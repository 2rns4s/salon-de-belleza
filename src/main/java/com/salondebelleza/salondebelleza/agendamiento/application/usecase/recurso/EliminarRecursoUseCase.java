package com.salondebelleza.salondebelleza.agendamiento.application.usecase.recurso;

import com.salondebelleza.salondebelleza.agendamiento.application.mapper.RecursoDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.RecursoNoEncontradoException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.RecursoRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EliminarRecursoUseCase {

    private final RecursoRepositoryPort recursoRepositoryPort;
    private final RecursoDTOMapper recursoDTOMapper;

    @Autowired
    public EliminarRecursoUseCase(RecursoRepositoryPort recursoRepositoryPort, RecursoDTOMapper recursoDTOMapper) {
        this.recursoRepositoryPort = recursoRepositoryPort;
        this.recursoDTOMapper = recursoDTOMapper;
    }

    public void executeEliminar (Long idRecurso){
        this.recursoRepositoryPort.findById(idRecurso).orElseThrow(
                () -> new RecursoNoEncontradoException("Recurso con id " + idRecurso + " no encontrado")
        );
        this.recursoRepositoryPort.delete(idRecurso);
    }
}
