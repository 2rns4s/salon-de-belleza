package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.repository;

import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.EmpleadoEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<EmpleadoEntity, Long> {

    @EntityGraph(attributePaths = {"franjasLaborales", "calificaciones", "calificaciones.servicioEntity"})
    Optional<EmpleadoEntity> findById(Long idEmpleado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EmpleadoEntity e WHERE e.idEmpleado = :idEmpleado")
    Optional<EmpleadoEntity> findByIdEmpleadoWithLock(@Param("idEmpleado") Long idEmpleado);

    Boolean existsByCorreo(String correo);

    List<EmpleadoEntity> findAll();
}
