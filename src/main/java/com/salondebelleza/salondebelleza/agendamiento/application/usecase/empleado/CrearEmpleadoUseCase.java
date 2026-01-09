package com.salondebelleza.salondebelleza.agendamiento.application.usecase.empleado;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.empleado.CrearEmpleadoRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.empleado.EmpleadoResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.EmpleadoDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Servicio;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.EmpleadoExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.ServicioNoEncontradoException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.EmpleadoRepositoryPort;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.ServicioRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CrearEmpleadoUseCase {

    private final EmpleadoRepositoryPort empleadoRepositoryPort;
    private final EmpleadoDTOMapper empleadoDTOMapper;
    private final ServicioRepositoryPort servicioRepositoryPort;

    @Autowired
    public CrearEmpleadoUseCase(EmpleadoRepositoryPort empleadoRepositoryPort, EmpleadoDTOMapper empleadoDTOMapper, ServicioRepositoryPort servicioRepositoryPort) {
        this.empleadoRepositoryPort = empleadoRepositoryPort;
        this.empleadoDTOMapper = empleadoDTOMapper;
        this.servicioRepositoryPort = servicioRepositoryPort;
    }

    public EmpleadoResponse execute(CrearEmpleadoRequest request) {
        if (this.empleadoRepositoryPort.existsByCorreo(request.correo())) {
            throw new EmpleadoExistenteException("El correo " + request.correo() + " ya existe");
        }

        List<Servicio> serviciosParaAsignar = new ArrayList<>();

        if (request.serviciosIds() != null && !request.serviciosIds().isEmpty()) {
            for (Long idServicio : request.serviciosIds()) {
                Servicio servicio = servicioRepositoryPort.findById(idServicio)
                        .orElseThrow(() -> new ServicioNoEncontradoException("El servicio con id " + idServicio + " no existe"));

                serviciosParaAsignar.add(servicio);
            }
        }

        return this.empleadoDTOMapper
                .toEmpleadoResponse(this.empleadoRepositoryPort
                        .save(empleadoDTOMapper
                                .toEmpleado(request)));

    }
}
