package com.salondebelleza.salondebelleza.agendamiento.application.usecase.recurso;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.recursos.RecursoResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.RecursoDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.RecursoRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarRecursoUseCase {

    private final RecursoRepositoryPort recursoRepositoryPort;
    private final RecursoDTOMapper recursoDTOMapper;

    @Autowired
    public ListarRecursoUseCase(RecursoRepositoryPort recursoRepositoryPort, RecursoDTOMapper recursoDTOMapper) {
        this.recursoRepositoryPort = recursoRepositoryPort;
        this.recursoDTOMapper = recursoDTOMapper;
    }

    public List<RecursoResponse> executeByAll(){
        return this.recursoRepositoryPort.findAll()
                .stream()
                .map(recursoDTOMapper::toRecursoResponse)
                .toList();
    }
}
