package com.salondebelleza.salondebelleza.agendamiento.application.usecase.reporte;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.reporte.DashboardResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.reporte.EstadoCitaCountDTO;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.reporte.RendimientoDTO;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.reporte.ServicioTopDTO;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.RangoFechasInvalidoException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.CitaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenerarDashboardUseCase {

    private final CitaRepositoryPort citaRepositoryPort;

    @Transactional(readOnly = true)
    public DashboardResponse execute (LocalDateTime fechaInicio, LocalDateTime fechaFin) {

        if (fechaInicio.isAfter(fechaFin)) {
            throw new RangoFechasInvalidoException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        };

        List<EstadoCitaCountDTO> estadoCitas = citaRepositoryPort.contarCitasPorEstadoEnRango(fechaInicio, fechaFin);
        List<ServicioTopDTO> serviciosTop = citaRepositoryPort.encontrarTopServiciosRealizados(fechaInicio, fechaFin, 5);

        RendimientoDTO rendimiento = procesarRendimiento(estadoCitas);

        return new DashboardResponse(rendimiento, serviciosTop);
    }

    private RendimientoDTO procesarRendimiento(List<EstadoCitaCountDTO> estadoCitas) {
        long completadas = 0;
        long canceladas = 0;
        long total = 0;

        for (EstadoCitaCountDTO conteo : estadoCitas){

            long cantidad = conteo.cantidad();
            total += cantidad;

            switch (conteo.estado()) {
                case REALIZADA -> completadas += cantidad;
                case CANCELADA -> canceladas += cantidad;
            }
        }

        double porcentajeCompletadas = total > 0 ? (double) completadas / total * 100 : 0.0;

        double tasaRedondeada = Math.round(porcentajeCompletadas * 100.0) / 100.0;

        return new RendimientoDTO(total, completadas, canceladas, tasaRedondeada);
    }

}
