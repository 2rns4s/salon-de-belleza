package com.salondebelleza.salondebelleza.agendamiento.application.usecase.empleado;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.empleado.ActualizarEmpleadoRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.empleado.EmpleadoResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.EmpleadoDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Empleado;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.EmpleadoExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.EmpleadoNoExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.EmpleadoRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActualizarEmpleadoUseCase {

    private final EmpleadoRepositoryPort empleadoRepositoryPort;
    private final EmpleadoDTOMapper empleadoDTOMapper;

    @Autowired
    public ActualizarEmpleadoUseCase(EmpleadoRepositoryPort empleadoRepositoryPort, EmpleadoDTOMapper empleadoDTOMapper) {
        this.empleadoRepositoryPort = empleadoRepositoryPort;
        this.empleadoDTOMapper = empleadoDTOMapper;
    }

    public EmpleadoResponse execute(Long idEmpleado, ActualizarEmpleadoRequest request) {
        Empleado empleado = this.empleadoRepositoryPort.findById(idEmpleado).orElseThrow(
                () -> new EmpleadoNoExistenteException("El empleado con id " + idEmpleado + " no existe")
        );

        if (request.correo() != null && !request.correo().equals(empleado.getCorreo())) {
            if (this.empleadoRepositoryPort.existsByCorreo(request.correo())) {
                throw new EmpleadoExistenteException("El correo " + request.correo() + " ya existe");
            }
        }

        this.empleadoDTOMapper.updateEmpleadoFromRequest(request, empleado);

        return this.empleadoDTOMapper
                .toEmpleadoResponse(this.empleadoRepositoryPort
                        .save(empleado));
    }
}
