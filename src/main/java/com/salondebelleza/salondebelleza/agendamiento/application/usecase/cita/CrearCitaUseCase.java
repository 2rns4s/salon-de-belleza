package com.salondebelleza.salondebelleza.agendamiento.application.usecase.cita;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.cita.CitaResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.cita.CrearCitaRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.CitaDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.*;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.EmpleadoNoExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.RecursoNoEncontradoException;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.SolapamientoException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.CitaRepositoryPort;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.EmpleadoRepositoryPort;
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
    private final EmpleadoRepositoryPort empleadoRepositoryPort;

    @Autowired
    public CrearCitaUseCase(CitaRepositoryPort citaRepositoryPort, CitaDTOMapper citaDTOMapper, RecursoRepositoryPort recursoRepositoryPort, ServicioRepositoryPort servicioRepositoryPort, EmpleadoRepositoryPort empleadoRepositoryPort) {
        this.citaRepositoryPort = citaRepositoryPort;
        this.citaDTOMapper = citaDTOMapper;
        this.recursoRepositoryPort = recursoRepositoryPort;
        this.servicioRepositoryPort = servicioRepositoryPort;
        this.empleadoRepositoryPort = empleadoRepositoryPort;
    }

    @Transactional
    public CitaResponse execute(CrearCitaRequest request) {

        // 1. Obtener la duración del servicio para calcular la hora fin
        Servicio servicio = servicioRepositoryPort.findById(request.idServicio())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con id: " + request.idServicio()));

        LocalDateTime fechaInicio = request.fechaInicio();
        LocalDateTime fechaFin = fechaInicio.plusMinutes(servicio.getDuracion());

        this.empleadoRepositoryPort.findByIdEmpleadoWithLock(request.idEmpleado())
                .orElseThrow(() -> new EmpleadoNoExistenteException("Empleado no encontrado con id: " + request.idEmpleado()));

        // 2. Validar Disponibilidad del EMPLEADO
        if (citaRepositoryPort.existCitaSolapadaParaEmpleado(request.idEmpleado(), fechaInicio, fechaFin)) {
            throw new SolapamientoException("El empleado no está disponible en el horario seleccionado.");
        }

        // 3. Algoritmo de Asignación Automática de Recursos Físicos
        List<CitaRecursos> recursosAAsignar = new ArrayList<>();

        if (servicio.getServicioRecursos() != null) {
            for (ServicioRecursos requisito : servicio.getServicioRecursos()) {

                // Paso A: Obtenemos el recurso base (Ej: ID 1 "Silla", Total: 5)
                // OJO: Aquí asumimos que el servicio ya tiene vinculado el recurso real.
                // Si 'requisito' es solo una referencia intermedia, buscamos el recurso real:
                Recurso recursoBase = recursoRepositoryPort.findByIdRecursoWithLock(requisito.getIdRecurso())
                        .orElseThrow(() -> new RecursoNoEncontradoException("Recurso no encontrado"));

                // Paso B: Contamos cuántas citas están usando este recurso AHORA MISMO
                int usoActual = citaRepositoryPort.contarCitasSolapadasPorRecurso(
                        recursoBase.getIdRecurso(),
                        fechaInicio,
                        fechaFin
                );

                // Paso C: La Lógica Matemática
                // Si hay 3 ocupados y el total es 5, ¡podemos agendar! (3 < 5)
                if (usoActual >= recursoBase.getCantidadTotal()) {
                    throw new SolapamientoException(
                            "No hay disponibilidad de: " + recursoBase.getNombre() +
                                    ". (Ocupados: " + usoActual + "/" + recursoBase.getCantidadTotal() + ")"
                    );
                }

                // Paso D: Asignamos el MISMO ID (1).
                // La base de datos tendrá muchas filas en 'cita_recursos' apuntando al recurso 1,
                // pero el 'count' de arriba controlará que no excedamos el límite.
                CitaRecursos asignacion = new CitaRecursos();
                asignacion.setIdRecurso(recursoBase.getIdRecurso());
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
