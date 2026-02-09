package com.salondebelleza.salondebelleza.agendamiento.application.usecase.disponibilidad;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.disponibilidad.DisponibilidadRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.disponibilidad.DisponibilidadResponse;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.*;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.EmpleadoNoExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.ServicioNoEncontradoException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.CitaRepositoryPort;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.EmpleadoRepositoryPort;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.RecursoRepositoryPort;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.ServicioRepositoryPort;
import com.salondebelleza.salondebelleza.agendamiento.domain.valueobjects.FranjaLaboral;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ObtenerDisponibilidadUseCase {

    private final CitaRepositoryPort citaRepositoryPort;
    private final EmpleadoRepositoryPort empleadoRepositoryPort;
    private final ServicioRepositoryPort servicioRepositoryPort;
    private final RecursoRepositoryPort recursoRepositoryPort;

    private final LocalTime horaInicioSalon;
    private final LocalTime horaFinSalon;
    private final int INTERVALO_MINIMO_CITAS = 30;

    public ObtenerDisponibilidadUseCase(
            CitaRepositoryPort citaRepositoryPort,
            EmpleadoRepositoryPort empleadoRepositoryPort,
            ServicioRepositoryPort servicioRepositoryPort,
            RecursoRepositoryPort recursoRepositoryPort,
            @Value("${salon.hora-inicio-servicio}") LocalTime horaInicioSalon,
            @Value("${salon.hora-fin-servicio}") LocalTime horaFinSalon) {
        this.citaRepositoryPort = citaRepositoryPort;
        this.empleadoRepositoryPort = empleadoRepositoryPort;
        this.servicioRepositoryPort = servicioRepositoryPort;
        this.recursoRepositoryPort = recursoRepositoryPort;
        this.horaInicioSalon = horaInicioSalon;
        this.horaFinSalon = horaFinSalon;
    }

    @Transactional(readOnly = true)
    public DisponibilidadResponse execute(DisponibilidadRequest request) {

        Servicio servicio = this.servicioRepositoryPort.findById(request.idServicio()).orElseThrow(
                () -> new ServicioNoEncontradoException("El servicio con id " + request.idServicio() + " no existe"));

        int duracionServicio = servicio.getDuracion();

        List<Long> recursosRequeridosIds = (servicio.getServicioRecursos() != null)
                ? servicio.getServicioRecursos().stream().map(ServicioRecursos::getIdRecurso).toList()
                : Collections.emptyList();

        Map<Long, Integer> inventarioDeRecursos = this.recursoRepositoryPort.findAll()
                .stream()
                .collect(Collectors.toMap(Recurso::getIdRecurso, Recurso::getCantidadTotal));

        LocalDateTime inicioDia = request.fecha().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1);

        List<Cita> todasLasCitasDelDia = citaRepositoryPort.findAllByRangoFecha(inicioDia, finDia);

        List<FranjaLaboral> franjasEmpleado;
        List<Cita> citasDelEmpleado;

        if (request.idEmpleado() != null) {
            Empleado empleado = empleadoRepositoryPort.findById(request.idEmpleado())
                    .orElseThrow(() -> new EmpleadoNoExistenteException("Empleado no encontrado"));

            franjasEmpleado = empleado.getFranjasLaborales();

            citasDelEmpleado = todasLasCitasDelDia.stream()
                    .filter(c -> c.getIdEmpleado().equals(request.idEmpleado()))
                    .toList();
        } else {
            return new DisponibilidadResponse(request.fecha(), Collections.emptyList());
        }

        List<LocalTime> slotsDisponibles = new ArrayList<>();
        LocalTime slotActual = horaInicioSalon;

        while (!slotActual.plusMinutes(duracionServicio).isAfter(horaFinSalon)) {
            LocalTime inicio = slotActual;
            LocalTime fin = slotActual.plusMinutes(duracionServicio);

            boolean empleadoDisponible = esHorarioLaboral(inicio, fin, request.fecha(), franjasEmpleado) &&
                    !hayConflictoConCitas(inicio, fin, citasDelEmpleado);

            boolean recursosDisponibles = empleadoDisponible && hayStockDeRecursos(inicio, fin, recursosRequeridosIds, inventarioDeRecursos, todasLasCitasDelDia);

            if (recursosDisponibles) {
                slotsDisponibles.add(inicio);
            }

            slotActual = slotActual.plusMinutes(INTERVALO_MINIMO_CITAS);
        }
        return new DisponibilidadResponse(request.fecha(), slotsDisponibles);
    }

    private boolean esHorarioLaboral(LocalTime inicio, LocalTime fin, LocalDate fecha, List<FranjaLaboral> franjas) {
        // CORRECCIÓN
        return franjas.stream().anyMatch(franja ->
                franja.getDia().equals(fecha.getDayOfWeek())
                        && !inicio.isBefore(franja.getHoraInicio())
                        && !fin.isAfter(franja.getHoraFin())
        );
    }

    private boolean hayConflictoConCitas(LocalTime inicio, LocalTime fin, List<Cita> citas) {
        return citas.stream().anyMatch(cita ->
                seSolapan(inicio, fin, cita.getFechaInicio().toLocalTime(), cita.getFechaFin().toLocalTime())
        );
    }

    private boolean seSolapan(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    private boolean hayStockDeRecursos(LocalTime inicio, LocalTime fin, List<Long> recursosIds, Map<Long, Integer> inventarioTotal, List<Cita> todasLasCitas
    ) {
        if (recursosIds.isEmpty()) return true;

        List<Cita> citasSimultaneas = todasLasCitas.stream()
                .filter(c ->
                        seSolapan(
                                inicio,
                                fin,
                                c.getFechaInicio().toLocalTime(),
                                c.getFechaFin().toLocalTime())).toList();

        for (Long recursoId : recursosIds) {
            int capacidadTotal = inventarioTotal.getOrDefault(recursoId, 0);

            long usadosAhora = citasSimultaneas.stream()
                    .flatMap(c -> (c.getCitaRecursos() != null ? c.getCitaRecursos().stream() : Stream.empty()))
                    .filter(cr -> cr.getIdRecurso().equals(recursoId))
                    .count();

            if (usadosAhora >= capacidadTotal) return false;
        }
        return true;
    }
}
