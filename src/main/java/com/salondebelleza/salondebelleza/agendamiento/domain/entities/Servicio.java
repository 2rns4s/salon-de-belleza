package com.salondebelleza.salondebelleza.agendamiento.domain.entities;

import java.util.List;

public class Servicio {
    private long idServicio;
    private String nombre;
    private int duracion;
    List<Calificacion> calificaciones;
    List<ServicioRecurso> serviciosRecursos;
    List<Cita> citas;
}
