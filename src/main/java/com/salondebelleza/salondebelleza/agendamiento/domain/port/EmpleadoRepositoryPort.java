package com.salondebelleza.salondebelleza.agendamiento.domain.port;

import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Empleado;

import java.util.List;
import java.util.Optional;

public interface EmpleadoRepositoryPort {

    Empleado save(Empleado empleado);

    Optional<Empleado> findById(long idEmpleado);

    List<Empleado> findAll();

    void deleteById(long idEmpleado);

    boolean existsByCorreo(String correo);

    Optional<Empleado> findByIdEmpleadoWithLock(Long idEmpleado);

    boolean existByIdEmpleado(Long idEmpleado);
}
