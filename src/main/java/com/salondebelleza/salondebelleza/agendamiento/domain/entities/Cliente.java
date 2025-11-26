package com.salondebelleza.salondebelleza.agendamiento.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Cliente {
    private Long idCliente;
    private String nombre;
    private String apellido;
    private String documento;
    private String telefono;
    List<Cita> citas;
}
