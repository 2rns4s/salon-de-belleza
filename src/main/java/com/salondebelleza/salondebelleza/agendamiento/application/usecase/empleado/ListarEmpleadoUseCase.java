package com.salondebelleza.salondebelleza.agendamiento.application.usecase.empleado;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.empleado.EmpleadoResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.EmpleadoDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.EmpleadoRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarEmpleadoUseCase {

    private final EmpleadoRepositoryPort empleadoRepositoryPort;
    private final EmpleadoDTOMapper empleadoDTOMapper;

    @Autowired
    public ListarEmpleadoUseCase(EmpleadoRepositoryPort empleadoRepositoryPort, EmpleadoDTOMapper empleadoDTOMapper) {
        this.empleadoRepositoryPort = empleadoRepositoryPort;
        this.empleadoDTOMapper = empleadoDTOMapper;
    }

    public List<EmpleadoResponse> execute() {
        return this.empleadoRepositoryPort.findAll()
                .stream()
                .map(empleadoDTOMapper::toEmpleadoResponse)
                .toList();
    }
}
