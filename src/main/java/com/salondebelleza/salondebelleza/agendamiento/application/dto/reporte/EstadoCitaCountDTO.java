package com.salondebelleza.salondebelleza.agendamiento.application.dto.reporte;

import com.salondebelleza.salondebelleza.agendamiento.domain.entities.EstadoCita;

public record EstadoCitaCountDTO(
        EstadoCita estado,
        Long cantidad
) {
}
