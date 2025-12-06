package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence;

import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Servicio;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.RecursoNoEncontradoException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.ServicioRepositoryPort;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.RecursoEntity;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.ServicioEntity;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.ServicioRecursosEntity;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.mapper.RecursoMapper;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.mapper.ServicioMapper;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.repository.RecursoRepository;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ServicioPersistenceAdapter implements ServicioRepositoryPort {

    private final ServicioMapper servicioMapper;
    private final ServicioRepository servicioRepository;
    private final RecursoRepository recursoRepository;

    @Autowired
    public ServicioPersistenceAdapter(ServicioMapper servicioMapper, ServicioRepository servicioRepository, RecursoRepository recursoRepository, RecursoMapper recursoMapper) {
        this.servicioMapper = servicioMapper;
        this.servicioRepository = servicioRepository;
        this.recursoRepository = recursoRepository;
    }


    @Override
    public Servicio save(Servicio servicio) {
        ServicioEntity servicioEntity = servicioMapper.toServicioEntity(servicio);

        if (servicioEntity.getIdServicio() == null || servicioEntity.getIdServicio() == 0) {
            servicioEntity.setIdServicio(null);
        }

        servicioEntity.setServicioRecursos(new ArrayList<>());

        if (servicio.getServicioRecursos() != null ) {
            servicio.getServicioRecursos().forEach(
                    req -> {
                        RecursoEntity recurso = this.recursoRepository.findById(req.getIdRecurso())
                                .orElseThrow(() -> new RecursoNoEncontradoException("Recurso con id " + req.getIdRecurso() + " no encontrado"));
                        ServicioRecursosEntity relacion = new ServicioRecursosEntity();
                        relacion.setRecursoEntity(recurso);
                        relacion.setServicioEntity(servicioEntity);
                        relacion.setCantidadRequerida(req.getCantidadRequerida());
                        servicioEntity.getServicioRecursos().add(relacion);
                    }
            );
        }
        return this.servicioMapper.toServicio(this.servicioRepository.save(servicioEntity));
    }

    @Override
    public Optional<Servicio> findByNombreIgnoreCase(String nombre) {
        return this.servicioRepository
                .findByNombreIgnoreCase(nombre)
                .map(servicioMapper::toServicio);
    }

    @Override
    public Optional<Servicio> findById(long idServicio) {
        return this.servicioRepository.findById(idServicio)
                .map(servicioMapper::toServicio);
    }

    @Override
    public List<Servicio> findAll() {
        return this.servicioRepository.findAll()
                .stream().map(servicioMapper::toServicio)
                .toList();
    }

    @Override
    public void delete(Long idServicio) {
        this.servicioRepository.deleteById(idServicio);
    }
}
