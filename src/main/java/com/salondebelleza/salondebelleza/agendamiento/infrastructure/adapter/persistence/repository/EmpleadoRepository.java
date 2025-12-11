package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.repository;

import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.EmpleadoEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<EmpleadoEntity, Long> {

    @EntityGraph(attributePaths = {"franjasLaborales", "calificaciones", "calificaciones.servicioEntity"})
    Optional<EmpleadoEntity> findById(Long idEmpleado);

    Boolean existsByCorreo(String correo);

    List<EmpleadoEntity> findAll();
}
