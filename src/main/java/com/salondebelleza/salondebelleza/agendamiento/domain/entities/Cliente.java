package com.salondebelleza.salondebelleza.agendamiento.domain.entities;

import java.util.List;

public class Cliente {
    private long idCliente;
    private String nombre;
    private String apellido;
    private String documento;
    private String telefono;
    List<Cita> citas;
}
