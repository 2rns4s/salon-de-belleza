package com.salondebelleza.salondebelleza.agendamiento.application.usecase.servicio;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.servicio.CrearServicioRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.servicio.ServicioResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.ServicioDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Servicio;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.ServicioExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.ServicioNoEncontradoException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.ServicioRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ActualizarServicioUseCase {

    private final ServicioDTOMapper servicioDTOMapper;
    private final ServicioRepositoryPort servicioRepositoryPort;

    @Autowired
    public ActualizarServicioUseCase(ServicioDTOMapper servicioDTOMapper, ServicioRepositoryPort servicioRepositoryPort) {
        this.servicioDTOMapper = servicioDTOMapper;
        this.servicioRepositoryPort = servicioRepositoryPort;
    }

    public ServicioResponse execute (Long idServicio, CrearServicioRequest request) {
        Servicio servicio = this.servicioRepositoryPort.findById(idServicio).orElseThrow(
                () -> new ServicioNoEncontradoException("Servicio con id " + idServicio + " no encontrado")
        );

        Optional<Servicio> nombreExistente = this.servicioRepositoryPort.findByNombreIgnoreCase(request.nombre());

        if (nombreExistente.isPresent()){
            if (!idServicio.equals(nombreExistente.get().getIdServicio())){
                throw new ServicioExistenteException("El servicio " + request.nombre() + " ya existe");
            }
        }

        Servicio servicioActualizado = this.servicioDTOMapper.toServicio(request);

        servicioActualizado.setIdServicio(servicio.getIdServicio());

        return this.servicioDTOMapper.toServicioResponse(this.servicioRepositoryPort.save(servicioActualizado));
    }
}
