package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.repository;

import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.CitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<CitaEntity, Long> {

    @Query("""
        SELECT COUNT(c) > 0 
        FROM CitaEntity c 
        WHERE c.empleado.idEmpleado = :idEmpleado 
        AND c.estado <> 'CANCELADA'
        AND (c.fechaInicio < :fechaFin AND c.fechaFin > :fechaInicio)
    """)
    boolean existsCitaSolapadaParaEmpleado(
            @Param("idEmpleado") Long idEmpleado,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    @Query("""
        SELECT COUNT(c) > 0 
        FROM CitaEntity c 
        JOIN c.recursosReservados cr
        WHERE cr.recursoEntity.idRecurso = :idRecurso
        AND c.estado <> 'CANCELADA'
        AND (c.fechaInicio < :fechaFin AND c.fechaFin > :fechaInicio)
    """)
    boolean existsCitaSolapadaParaRecurso(
            @Param("idRecurso") Long idRecurso,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    @Query("""
        SELECT COUNT(c) 
        FROM CitaEntity c 
        JOIN c.recursosReservados cr
        WHERE cr.recursoEntity.idRecurso = :idRecurso
        AND c.estado <> 'CANCELADA'
        AND (c.fechaInicio < :fechaFin AND c.fechaFin > :fechaInicio)
    """)
    int contarCitasSolapadasPorRecurso(
            @Param("idRecurso") Long idRecurso,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    @Query("SELECT c FROM CitaEntity c WHERE c.fechaInicio >= :start AND c.fechaInicio < :end")
    List<CitaEntity> findCitasDelDia(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
