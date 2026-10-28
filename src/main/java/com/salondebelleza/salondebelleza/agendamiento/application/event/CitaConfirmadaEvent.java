package com.salondebelleza.salondebelleza.agendamiento.application.event;

public record CitaConfirmadaEvent(
        Long idCita,
        String telefonoCliente,
        String nombreCliente,
        String nombreServicio,
        String fechaHoraCita
) {
}
