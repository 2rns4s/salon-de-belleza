package com.salondebelleza.salondebelleza.agendamiento.domain.entities;

import com.salondebelleza.salondebelleza.agendamiento.domain.valueobjects.FranjaLaboral;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Empleado {
    long idEmpleado;
    String nombre;
    String apellido;
    String correo;
    String telefono;
    List<Calificacion> calificaciones;
    List<FranjaLaboral> franjasLaborales;
    List<Cita> citas;
}