package com.salondebelleza.salondebelleza.agendamiento.application.usecase.servicio;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.servicio.CrearServicioRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.servicio.ServicioResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.ServicioDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.ServicioExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.ServicioRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CrearServicioUseCase {

    private final ServicioDTOMapper servicioMapper;
    private final ServicioRepositoryPort servicioRepositoryPort;

    @Autowired
    public CrearServicioUseCase(ServicioDTOMapper servicioMapper, ServicioRepositoryPort servicioRepositoryPort) {
        this.servicioMapper = servicioMapper;
        this.servicioRepositoryPort = servicioRepositoryPort;
    }

    public ServicioResponse execute(CrearServicioRequest request) {

        if (this.servicioRepositoryPort.findByNombreIgnoreCase(request.nombre()).isPresent()){
            throw new ServicioExistenteException("El servicio " + request.nombre() + " ya existe");
        }

        return this.servicioMapper.toServicioResponse(this.servicioRepositoryPort.save(servicioMapper.toServicio(request)));
    }
}
