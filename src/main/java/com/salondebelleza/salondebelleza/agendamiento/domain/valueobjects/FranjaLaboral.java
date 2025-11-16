package com.salondebelleza.salondebelleza.agendamiento.domain.valueobjects;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class FranjaLaboral {
    private int idFranjaLaboral;
    private int idEmpleado;
    private DayOfWeek dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
