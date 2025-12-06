package com.salondebelleza.salondebelleza.agendamiento.domain.port;

import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Servicio;


import java.util.List;
import java.util.Optional;

public interface ServicioRepositoryPort {

    Servicio save(Servicio servicio);

    Optional<Servicio> findByNombreIgnoreCase (String nombre);

    Optional<Servicio> findById(long idServicio);

    List<Servicio> findAll();

    void delete(Long idServicio);

}
