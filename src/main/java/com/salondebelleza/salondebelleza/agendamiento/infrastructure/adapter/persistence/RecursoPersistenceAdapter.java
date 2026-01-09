package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence;

import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Recurso;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.RecursoRepositoryPort;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.RecursoEntity;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.mapper.RecursoMapper;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.repository.RecursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RecursoPersistenceAdapter implements RecursoRepositoryPort {

    private final RecursoRepository recursoRepository;
    private final RecursoMapper recursoMapper;

    @Autowired
    public RecursoPersistenceAdapter(RecursoRepository recursoRepository, RecursoMapper recursoMapper) {
        this.recursoRepository = recursoRepository;
        this.recursoMapper = recursoMapper;
    }


    @Override
    public List<Recurso> findAll() {
        return this.recursoRepository.findAll()
                .stream().map(recursoMapper::toRecurso)
                .toList();
    }

    @Override
    public Recurso save(Recurso recurso) {

        RecursoEntity recursoEntity = recursoMapper.toRecursoEntity(recurso);

        if (recurso.getIdRecurso() == null || recurso.getIdRecurso() == 0) {
            recursoEntity.setIdRecurso(null);
        }

        return this.recursoMapper.toRecurso(recursoRepository.save(recursoEntity));
    }

    @Override
    public Optional<Recurso> findById(long idRecurso) {
        return this.recursoRepository.findById(idRecurso)
                .map(recursoMapper::toRecurso);
    }

    @Override
    public Optional<Recurso> findOneByNombreIgnoreCase(String nombre) {
        return this.recursoRepository.findFirstByNombreContainsIgnoreCase(nombre)
                .map(recursoMapper::toRecurso);
    }

    @Override
    public List<Recurso> findByNombreIgnoreCase(String nombre) {
        return this.recursoRepository.findByNombreIgnoreCase(nombre)
                .stream()
                .map(recursoMapper::toRecurso)
                .toList();
    }

    @Override
    public void delete(Long idRecurso) {
        recursoRepository.deleteById(idRecurso);
    }

    @Override
    public Optional<Recurso> findByIdRecursoWithLock(Long idRecurso) {
        return this.recursoRepository.findByIdRecursoWithLock(idRecurso)
                .map(recursoMapper::toRecurso);
    }
}
