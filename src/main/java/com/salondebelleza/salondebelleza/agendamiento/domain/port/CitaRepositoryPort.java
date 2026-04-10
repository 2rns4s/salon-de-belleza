package com.salondebelleza.salondebelleza.agendamiento.domain.port;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.reporte.EstadoCitaCountDTO;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.reporte.ServicioTopDTO;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Cita;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CitaRepositoryPort {

    Cita save(Cita cita);

    Optional<Cita> findById(Long idCita);

    void deleteById(Long idCita);

    List<Cita> findAll();

    boolean existCitaSolapadaParaEmpleado(Long idEmpleado, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    boolean existCitaSolapadaParaRecurso(Long idRecurso, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    int contarCitasSolapadasPorRecurso(Long idRecurso, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<Cita> findAllByRangoFecha(LocalDateTime inicioDia, LocalDateTime finDia);

    List<Cita> findAllByRangoFechaAndEmpleado(LocalDateTime inicioDia, LocalDateTime finDia, Long idEmpleado);

    List<EstadoCitaCountDTO> contarCitasPorEstadoEnRango(LocalDateTime inicio, LocalDateTime fin);

    List<ServicioTopDTO> encontrarTopServiciosRealizados(LocalDateTime inicio, LocalDateTime fin, int limit);
}

