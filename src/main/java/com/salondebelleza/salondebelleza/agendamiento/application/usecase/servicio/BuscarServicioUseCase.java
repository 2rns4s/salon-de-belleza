package com.salondebelleza.salondebelleza.agendamiento.application.usecase.servicio;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.servicio.ServicioResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.ServicioDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.ServicioNoEncontradoException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.ServicioRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuscarServicioUseCase {

    private final ServicioRepositoryPort servicioRepositoryPort;
    private final ServicioDTOMapper servicioDTOMapper;

    @Autowired
    public BuscarServicioUseCase(ServicioRepositoryPort servicioRepositoryPort, ServicioDTOMapper servicioDTOMapper) {
        this.servicioRepositoryPort = servicioRepositoryPort;
        this.servicioDTOMapper = servicioDTOMapper;
    }

    public ServicioResponse executeById(Long idServicio){
        return this.servicioDTOMapper.toServicioResponse(this.servicioRepositoryPort.findById(idServicio)
                .orElseThrow(() -> new ServicioNoEncontradoException("Servicio con id " + idServicio + " no encontrado")));
    }

    public ServicioResponse executeByNombre(String nombre){
        return this.servicioDTOMapper.toServicioResponse(this.servicioRepositoryPort.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new ServicioNoEncontradoException("Servicio con nombre " + nombre + " no encontrado")));
    }
}
