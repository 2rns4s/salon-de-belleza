package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "servicios")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ServicioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    Long idServicio;

    @Column(unique = true, nullable = false)
    String nombre;

    @Column(nullable = false)
    int duracion;

    @OneToMany(mappedBy = "servicioEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ServicioRecursosEntity> servicioRecursos;
}
