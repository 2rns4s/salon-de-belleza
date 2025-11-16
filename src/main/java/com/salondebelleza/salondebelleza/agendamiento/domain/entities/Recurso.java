package com.salondebelleza.salondebelleza.agendamiento.domain.entities;

import java.util.List;

public class Recurso {
    private long idRecurso;
    private String nombre;
    private int cantidadTotal;
    List<ServicioRecurso> serviciosRecursos;
    List<CitaRecurso> citaRecursos;
}
