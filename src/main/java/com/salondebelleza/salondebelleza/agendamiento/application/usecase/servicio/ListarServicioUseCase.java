package com.salondebelleza.salondebelleza.agendamiento.application.usecase.servicio;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.servicio.ServicioResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.ServicioDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.ServicioRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarServicioUseCase {

    private final ServicioRepositoryPort servicioRepositoryPort;
    private final ServicioDTOMapper servicioDTOMapper;

    @Autowired
    public ListarServicioUseCase(ServicioRepositoryPort servicioRepositoryPort, ServicioDTOMapper servicioDTOMapper) {
        this.servicioRepositoryPort = servicioRepositoryPort;
        this.servicioDTOMapper = servicioDTOMapper;
    }

    public List<ServicioResponse> execute(){
        return this.servicioRepositoryPort.findAll()
                .stream()
                .map(servicioDTOMapper::toServicioResponse)
                .toList();
    }
}
