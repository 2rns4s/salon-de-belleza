package com.salondebelleza.salondebelleza.agendamiento.domain.entities;

import com.salondebelleza.salondebelleza.agendamiento.domain.valueobjects.FranjaLaboral;

import java.util.List;

public class Empleado {
    long idEmpleado;
    String nombre;
    String apellido;
    List<Calificacion> calificaciones;
    List<FranjaLaboral> franjasLaborales;
    List<Cita> citas;
}