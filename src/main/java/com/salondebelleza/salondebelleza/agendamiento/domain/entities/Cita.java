package com.salondebelleza.salondebelleza.agendamiento.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Cita {
    private Long idCita;
    private Long idEmpleado;
    private Long idServicio;
    private Long idCliente;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private EstadoCita estado;
    private List<CitaRecursos> citaRecursos;
}
