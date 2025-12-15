package com.salondebelleza.salondebelleza.agendamiento.application.usecase.empleado;

import com.salondebelleza.salondebelleza.agendamiento.domain.port.EmpleadoRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EliminarEmpleadoUseCase {

    private final EmpleadoRepositoryPort empleadoRepositoryPort;

    @Autowired
    public EliminarEmpleadoUseCase(EmpleadoRepositoryPort empleadoRepositoryPort) {
        this.empleadoRepositoryPort = empleadoRepositoryPort;
    }

    public void execute(Long idEmpleado){
        this.empleadoRepositoryPort.deleteById(idEmpleado);
    }
}
