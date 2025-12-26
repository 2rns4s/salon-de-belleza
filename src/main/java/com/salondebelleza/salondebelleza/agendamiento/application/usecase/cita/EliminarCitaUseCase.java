package com.salondebelleza.salondebelleza.agendamiento.application.usecase.cita;

import com.salondebelleza.salondebelleza.agendamiento.domain.exception.CitaNoEncontradaException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.CitaRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EliminarCitaUseCase {

    private final CitaRepositoryPort citaRepositoryPort;

    @Autowired
    public EliminarCitaUseCase(CitaRepositoryPort citaRepositoryPort) {
        this.citaRepositoryPort = citaRepositoryPort;
    }

    public void execute(Long idCita){
        this.citaRepositoryPort.findById(idCita)
                .orElseThrow(() -> new CitaNoEncontradaException("Cita con id " + idCita + " no encontrada"));

        this.citaRepositoryPort.deleteById(idCita);
    }
}
