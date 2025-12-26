package com.salondebelleza.salondebelleza.agendamiento.application.usecase.cita;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.cita.CitaResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.cita.CrearCitaRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.CitaDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.*;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.SolapamientoException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.CitaRepositoryPort;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.RecursoRepositoryPort;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.ServicioRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CrearCitaUseCase {

    private final CitaRepositoryPort citaRepositoryPort;
    private final CitaDTOMapper citaDTOMapper;
    private final RecursoRepositoryPort recursoRepositoryPort;
    private final ServicioRepositoryPort servicioRepositoryPort;

    @Autowired
    public CrearCitaUseCase(CitaRepositoryPort citaRepositoryPort, CitaDTOMapper citaDTOMapper, RecursoRepositoryPort recursoRepositoryPort, ServicioRepositoryPort servicioRepositoryPort) {
        this.citaRepositoryPort = citaRepositoryPort;
        this.citaDTOMapper = citaDTOMapper;
        this.recursoRepositoryPort = recursoRepositoryPort;
        this.servicioRepositoryPort = servicioRepositoryPort;
    }

    @Transactional
    public CitaResponse execute(CrearCitaRequest request) {

        // 1. Obtener la duración del servicio para calcular la hora fin
        Servicio servicio = servicioRepositoryPort.findById(request.idServicio())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con id: " + request.idServicio()));

        LocalDateTime fechaInicio = request.fechaInicio();
        LocalDateTime fechaFin = fechaInicio.plusMinutes(servicio.getDuracion());

        // 2. Validar Disponibilidad del EMPLEADO
        if (citaRepositoryPort.existCitaSolapadaParaEmpleado(request.idEmpleado(), fechaInicio, fechaFin)) {
            throw new SolapamientoException("El empleado no está disponible en el horario seleccionado.");
        }

        // 3. Algoritmo de Asignación Automática de Recursos Físicos
        List<CitaRecursos> recursosAAsignar = new ArrayList<>();

        // Para cada requisito del servicio (Ej: "Silla", "Lavacabezas")
        if (servicio.getServicioRecursos() != null) {
            for (ServicioRecursos requisito : servicio.getServicioRecursos()) {

                // A. Buscamos TODOS los recursos físicos que coincidan con el nombre (Pool de recursos)
                List<Recurso> candidatos = recursoRepositoryPort.findByNombreIgnoreCase(requisito.getNombreRecurso());

                Recurso recursoDisponible = null;

                // B. Buscamos el primero que esté libre en ese horario
                for (Recurso candidato : candidatos) {
                    boolean ocupado = citaRepositoryPort.existCitaSolapadaParaRecurso(
                            candidato.getIdRecurso(), fechaInicio, fechaFin);

                    if (!ocupado) {
                        recursoDisponible = candidato;
                        break; // ¡Encontramos uno libre! Dejamos de buscar.
                    }
                }

                // C. Si no encontramos ninguno libre, fallamos
                if (recursoDisponible == null) {
                    throw new SolapamientoException("No hay disponibilidad de: " + requisito.getNombreRecurso() + " para este horario.");
                }

                // D. Preparamos la asignación
                CitaRecursos asignacion = new CitaRecursos();
                asignacion.setIdRecurso(recursoDisponible.getIdRecurso());
                recursosAAsignar.add(asignacion);
            }
        }

        // 4. Construir la Cita
        Cita nuevaCita = citaDTOMapper.toCita(request);
        nuevaCita.setFechaFin(fechaFin);
        nuevaCita.setEstado(EstadoCita.CONFIRMADA);
        nuevaCita.setCitaRecursos(recursosAAsignar);

        // 5. Guardar y Retornar
        return citaDTOMapper.toCitaResponse(citaRepositoryPort.save(nuevaCita));

    }
}
