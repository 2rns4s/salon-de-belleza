package com.salondebelleza.salondebelleza.agendamiento.application.usecase.recurso;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.recursos.ActualizarRecursoRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.recursos.RecursoResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.RecursoDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Recurso;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.RecursoExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.RecursoNoEncontradoException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.RecursoRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ActualizarRecursoUseCase {

    private final RecursoRepositoryPort recursoRepositoryPort;
    private final RecursoDTOMapper recursoDTOMapper;

    @Autowired
    public ActualizarRecursoUseCase(RecursoRepositoryPort recursoRepositoryPort, RecursoDTOMapper recursoDTOMapper) {
        this.recursoRepositoryPort = recursoRepositoryPort;
        this.recursoDTOMapper = recursoDTOMapper;
    }

    public RecursoResponse execute(Long idRecurso, ActualizarRecursoRequest request) {
        Recurso recurso = this.recursoRepositoryPort.findById(idRecurso).orElseThrow(
                () -> new RecursoNoEncontradoException("Recurso con id " + idRecurso + " no encontrado")
        );

        Optional<Recurso> nombreExistente = this.recursoRepositoryPort.findByNombre(request.nombre());

        if (nombreExistente.isPresent()){
            if (!idRecurso.equals(nombreExistente.get().getIdRecurso())){
                throw new RecursoExistenteException("El recurso " + request.nombre() + " ya existe");
            }
        }

        recurso.setNombre(request.nombre());
        recurso.setIdRecurso(idRecurso);
        recurso.setCantidadTotal(request.cantidadTotal());

        return this.recursoDTOMapper.toRecursoResponse(this.recursoRepositoryPort.save(recurso));
    }
}
