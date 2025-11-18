package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clientes")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;

    private String nombre;

    private String apellido;

    @Column(unique = true)
    private String documento;

    @Column(unique = true)
    private String telefono;
}
