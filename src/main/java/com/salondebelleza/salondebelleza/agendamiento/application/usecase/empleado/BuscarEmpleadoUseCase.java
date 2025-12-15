package com.salondebelleza.salondebelleza.agendamiento.application.usecase.empleado;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.empleado.EmpleadoResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.EmpleadoDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.EmpleadoNoExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.EmpleadoRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuscarEmpleadoUseCase {

    private final EmpleadoRepositoryPort empleadoRepositoryPort;
    private final EmpleadoDTOMapper empleadoDTOMapper;

    @Autowired
    public BuscarEmpleadoUseCase(EmpleadoRepositoryPort empleadoRepositoryPort, EmpleadoDTOMapper empleadoDTOMapper) {
        this.empleadoRepositoryPort = empleadoRepositoryPort;
        this.empleadoDTOMapper = empleadoDTOMapper;
    }

    public EmpleadoResponse buscarPorId(Long idEmpleado){
        return empleadoRepositoryPort.findById(idEmpleado)
                .map(empleadoDTOMapper::toEmpleadoResponse)
                .orElseThrow(() -> new EmpleadoNoExistenteException("Empleado no encontrado con id: " + idEmpleado));
    }
}
