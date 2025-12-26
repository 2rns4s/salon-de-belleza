package com.salondebelleza.salondebelleza.agendamiento.application.usecase.cita;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.cita.ActualizarCitaRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.cita.CitaResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.CitaDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Cita;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.CitaNoEncontradaException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.CitaRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActualizarCitaUseCase {

    private final CitaRepositoryPort citaRepositoryPort;
    private final CitaDTOMapper citaDTOMapper;

    @Autowired
    public ActualizarCitaUseCase(CitaRepositoryPort citaRepositoryPort, CitaDTOMapper citaDTOMapper) {
        this.citaRepositoryPort = citaRepositoryPort;
        this.citaDTOMapper = citaDTOMapper;
    }

    public CitaResponse execute(Long idCita, ActualizarCitaRequest request){
        Cita cita = this.citaRepositoryPort.findById(idCita)
                .orElseThrow(() -> new CitaNoEncontradaException("Cita con id " + idCita + " no encontrada"));

        cita.setEstado(request.estado());

        return this.citaDTOMapper.toCitaResponse(this.citaRepositoryPort.save(cita));
    }
}
