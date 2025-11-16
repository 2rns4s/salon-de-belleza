package com.salondebelleza.salondebelleza.agendamiento.domain.entities;

import java.time.LocalDateTime;
import java.util.List;

public class Cita {
    private long idCita;
    private long idEmpleado;
    private long idServicio;
    private long idCliente;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private List<CitaRecurso> citaRecursos;
}
