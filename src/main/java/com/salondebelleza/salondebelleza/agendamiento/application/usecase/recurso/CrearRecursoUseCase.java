package com.salondebelleza.salondebelleza.agendamiento.application.usecase.recurso;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.recursos.CrearRecursoRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.recursos.RecursoResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.RecursoDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.RecursoExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.RecursoRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CrearRecursoUseCase {

    private final RecursoRepositoryPort recursoRepositoryPort;
    private final RecursoDTOMapper recursoDTOMapper;

    @Autowired
    public CrearRecursoUseCase(RecursoRepositoryPort recursoRepositoryPort, RecursoDTOMapper recursoDTOMapper) {
        this.recursoRepositoryPort = recursoRepositoryPort;
        this.recursoDTOMapper = recursoDTOMapper;
    }

    public RecursoResponse execute(CrearRecursoRequest request) {

        this.recursoRepositoryPort.findByNombre(request.nombre()).ifPresent(
                recursoExistente -> {throw new RecursoExistenteException("El recurso " + request.nombre() + " ya existe");}
        );

        return this.recursoDTOMapper.toRecursoResponse(this.recursoRepositoryPort.save(this.recursoDTOMapper.toRecurso(request)));
    }
}
