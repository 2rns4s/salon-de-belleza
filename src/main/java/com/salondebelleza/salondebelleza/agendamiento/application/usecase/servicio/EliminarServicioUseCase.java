package com.salondebelleza.salondebelleza.agendamiento.application.usecase.servicio;

import com.salondebelleza.salondebelleza.agendamiento.application.mapper.ServicioDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.ServicioNoEncontradoException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.ServicioRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EliminarServicioUseCase {

    private final ServicioRepositoryPort servicioRepositoryPort;

    @Autowired
    public EliminarServicioUseCase(ServicioRepositoryPort servicioRepositoryPort, ServicioDTOMapper servicioDTOMapper) {
        this.servicioRepositoryPort = servicioRepositoryPort;
    }

    public void execute (Long idServicio) {
        this.servicioRepositoryPort.findById(idServicio)
                .orElseThrow(() -> new ServicioNoEncontradoException("El servicio con id " + idServicio + " no fue encontrado"));

        this.servicioRepositoryPort.delete(idServicio);
    }
}
