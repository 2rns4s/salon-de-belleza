package com.salondebelleza.salondebelleza.agendamiento.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Calificacion {
    private long idCalificacion;
    private long idEmpleado;
    private long idServicio;
}
