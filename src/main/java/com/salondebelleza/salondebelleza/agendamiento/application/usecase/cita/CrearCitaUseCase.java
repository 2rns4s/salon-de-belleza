package com.salondebelleza.salondebelleza.agendamiento.application.usecase.cita;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.cita.CitaResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.cita.CrearCitaRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.event.CitaConfirmadaEvent;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.CitaDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.*;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.*;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CrearCitaUseCase {

    private final CitaRepositoryPort citaRepositoryPort;
    private final CitaDTOMapper citaDTOMapper;
    private final RecursoRepositoryPort recursoRepositoryPort;
    private final ServicioRepositoryPort servicioRepositoryPort;
    private final EmpleadoRepositoryPort empleadoRepositoryPort;
    private final ClienteRepositoryPort clienteRepositoryPort;
    private final NotificacionEventPort notificacionEventPort;

    private final LocalTime horaInicioSalon;
    private final LocalTime horaFinSalon;

    @Autowired
    public CrearCitaUseCase(CitaRepositoryPort citaRepositoryPort,
                            CitaDTOMapper citaDTOMapper,
                            RecursoRepositoryPort recursoRepositoryPort,
                            ServicioRepositoryPort servicioRepositoryPort,
                            EmpleadoRepositoryPort empleadoRepositoryPort,
                            ClienteRepositoryPort clienteRepositoryPort,
                            NotificacionEventPort notificacionEventPort,
                            @Value("${salon.hora-inicio-servicio}") LocalTime horaInicioSalon,
                            @Value("${salon.hora-fin-servicio}") LocalTime horaFinSalon) {
        this.citaRepositoryPort = citaRepositoryPort;
        this.citaDTOMapper = citaDTOMapper;
        this.recursoRepositoryPort = recursoRepositoryPort;
        this.servicioRepositoryPort = servicioRepositoryPort;
        this.empleadoRepositoryPort = empleadoRepositoryPort;
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.notificacionEventPort = notificacionEventPort;
        this.horaInicioSalon = horaInicioSalon;
        this.horaFinSalon = horaFinSalon;
    }

    @Transactional
    public CitaResponse execute(CrearCitaRequest request) {

        Cliente cliente = this.clienteRepositoryPort.findById(request.idCliente())
                .orElseThrow(() -> new ClienteNoExistenteException("El cliente con id " + request.idCliente() + " no existe"));

        // PASO CRÍTICO 1: Bloquear el empleado PRIMERO para evitar race conditions
        // Esto adquiere un lock pesimista sobre la fila del empleado
        Empleado empleado = this.empleadoRepositoryPort.findByIdEmpleadoWithFranjasAndLock(request.idEmpleado())
                .orElseThrow(() -> new EmpleadoNoExistenteException("El empleado con id " + request.idEmpleado() + " no existe"));

        // 2. Obtener la duración del servicio para calcular la hora fin
        Servicio servicio = servicioRepositoryPort.findById(request.idServicio())
                .orElseThrow(() -> new ServicioNoEncontradoException("Servicio no encontrado con id: " + request.idServicio()));

        LocalDateTime fechaInicio = request.fechaInicio();
        LocalDateTime fechaFin = fechaInicio.plusMinutes(servicio.getDuracion());

        if (fechaInicio.toLocalTime().isBefore(horaInicioSalon) ||
                fechaFin.toLocalTime().isAfter(horaFinSalon)) {
            throw new CitaFueraDeHorarioException(
                    "La cita está fuera del horario de operación del salón (" +
                            horaInicioSalon + " - " + horaFinSalon + ")."
            );
        }

        if (fechaInicio.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se pueden agendar citas en el pasado.");
        }

        boolean trabajaEnEsaHora = empleado.getFranjasLaborales().stream()
                .anyMatch(franja ->
                        franja.getDia().equals(fechaInicio.getDayOfWeek()) &&
                                !fechaInicio.toLocalTime().isBefore(franja.getHoraInicio()) &&
                                !fechaFin.toLocalTime().isAfter(franja.getHoraFin())
                );

        if (!trabajaEnEsaHora) {
            throw new EmpleadoNoTrabajaEnEseHorarioException("El empleado no trabaja en el horario seleccionado.");
        }

        // 3. Validar Disponibilidad del EMPLEADO (ahora con lock adquirido previamente)
        if (citaRepositoryPort.existCitaSolapadaParaEmpleado(request.idEmpleado(), fechaInicio, fechaFin)) {
            throw new SolapamientoException("El empleado no está disponible en el horario seleccionado.");
        }

        // 3. Algoritmo de Asignación Automática de Recursos Físicos
        List<CitaRecursos> recursosAAsignar = new ArrayList<>();

        if (servicio.getServicioRecursos() != null) {

            List<ServicioRecursos> recursosOrdenados = servicio.getServicioRecursos().stream()
                    .sorted((r1, r2) -> Long.compare(r1.getIdRecurso(), r2.getIdRecurso())) // Ojo: asegúrate de ordenar por el ID del RECURSO, no de la tabla intermedia
                    .toList();

            for (ServicioRecursos requisito : recursosOrdenados) {

                // Paso A: Obtenemos el recurso base (Ej: ID 1 "Silla", Total: 5)
                // Aquí asumimos que el servicio ya tiene vinculado el recurso real.
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

        CitaResponse savedCita = this.citaDTOMapper.toCitaResponse(this.citaRepositoryPort.save(nuevaCita));

        notificacionEventPort.enviarCitaConfirmada(
                new CitaConfirmadaEvent(
                        savedCita.idCita(),
                        cliente.getTelefono(),
                        cliente.getNombre(),
                        servicio.getNombre(),
                        savedCita.fechaInicio().toString()
                )
        );

        // 5. Guardar y Retornar
        return savedCita;

    }
}
