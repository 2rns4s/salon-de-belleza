package com.salondebelleza.salondebelleza.agendamiento.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class CitaRecursos {
    private Long idCitaRecurso;
    private Long idCita;
    private Long idRecurso;
}
