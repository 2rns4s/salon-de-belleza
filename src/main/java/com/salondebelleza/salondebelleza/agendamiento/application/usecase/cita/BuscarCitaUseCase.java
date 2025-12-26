package com.salondebelleza.salondebelleza.agendamiento.application.usecase.cita;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.cita.CitaResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.CitaDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.CitaNoEncontradaException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.CitaRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuscarCitaUseCase {

    private final CitaRepositoryPort citaRepositoryPort;
    private final CitaDTOMapper citaDTOMapper;

    @Autowired
    public BuscarCitaUseCase(CitaRepositoryPort citaRepositoryPort, CitaDTOMapper citaDTOMapper) {
        this.citaRepositoryPort = citaRepositoryPort;
        this.citaDTOMapper = citaDTOMapper;
    }

    public CitaResponse executeById (Long idCita) {
        return this.citaRepositoryPort.findById(idCita).map(citaDTOMapper::toCitaResponse).orElseThrow(
                () -> new CitaNoEncontradaException("Cita con id " + idCita + " no encontrada")
        );
    }
}
