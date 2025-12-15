package com.salondebelleza.salondebelleza.agendamiento.application.usecase.empleado;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.empleado.CrearEmpleadoRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.empleado.EmpleadoResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.EmpleadoDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.EmpleadoExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.EmpleadoRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CrearEmpleadoUseCase {

    private final EmpleadoRepositoryPort empleadoRepositoryPort;
    private final EmpleadoDTOMapper empleadoDTOMapper;

    @Autowired
    public CrearEmpleadoUseCase(EmpleadoRepositoryPort empleadoRepositoryPort, EmpleadoDTOMapper empleadoDTOMapper) {
        this.empleadoRepositoryPort = empleadoRepositoryPort;
        this.empleadoDTOMapper = empleadoDTOMapper;
    }

    public EmpleadoResponse execute(CrearEmpleadoRequest request) {
        if (this.empleadoRepositoryPort.existsByCorreo(request.correo())) {
            throw new EmpleadoExistenteException("El correo " + request.correo() + " ya existe");
        }

        return this.empleadoDTOMapper
                .toEmpleadoResponse(this.empleadoRepositoryPort
                        .save(empleadoDTOMapper
                                .toEmpleado(request)));

    }
}
