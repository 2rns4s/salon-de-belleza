package com.salondebelleza.salondebelleza.agendamiento.application.usecase.recurso;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.recursos.RecursoResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.RecursoDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.RecursoNoEncontradoException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.RecursoRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BuscarRecursoUseCase {

    private final RecursoRepositoryPort recursoRepositoryPort;
    private final RecursoDTOMapper recursoDTOMapper;

    @Autowired
    public BuscarRecursoUseCase(RecursoRepositoryPort recursoRepositoryPort, RecursoDTOMapper recursoDTOMapper) {
        this.recursoRepositoryPort = recursoRepositoryPort;
        this.recursoDTOMapper = recursoDTOMapper;
    }

    public RecursoResponse executeById(Long idRecurso){
        return this.recursoDTOMapper.toRecursoResponse(recursoRepositoryPort.findById(idRecurso).orElseThrow(
                () -> new RecursoNoEncontradoException("Recurso con id " + idRecurso + " no encontrado")
        ));
    }

    public RecursoResponse executeByNombre(String nombre){
        return this.recursoDTOMapper.toRecursoResponse(this.recursoRepositoryPort.findByNombre(nombre).orElseThrow(
                () -> new RecursoNoEncontradoException("Recurso con nombre " + nombre + " no encontrado")
        ));
    }
}
