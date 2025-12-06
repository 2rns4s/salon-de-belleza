package com.salondebelleza.salondebelleza.agendamiento.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ServicioRecursos {
    private long idServicioRecurso;
    private long idServicio;
    private long idRecurso;
    private String nombreRecurso;
    private int cantidadRequerida;
}
