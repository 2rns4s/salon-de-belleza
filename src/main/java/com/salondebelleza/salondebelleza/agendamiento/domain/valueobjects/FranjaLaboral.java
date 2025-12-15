package com.salondebelleza.salondebelleza.agendamiento.domain.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class FranjaLaboral {
    private Long idFranjaLaboral;
    private Long idEmpleado;
    private DayOfWeek dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
