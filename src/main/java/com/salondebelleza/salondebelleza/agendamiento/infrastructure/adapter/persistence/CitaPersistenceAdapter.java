package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence;

import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Cita;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.CitaRepositoryPort;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.CitaEntity;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.mapper.CitaMapper;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class CitaPersistenceAdapter implements CitaRepositoryPort {

    private final CitaRepository citaRepository;
    private final CitaMapper citaMapper;

    @Autowired
    public CitaPersistenceAdapter(CitaRepository citaRepository, CitaMapper citaMapper) {
        this.citaRepository = citaRepository;
        this.citaMapper = citaMapper;
    }


    @Override
    public Cita save(Cita cita) {
        CitaEntity citaEntity = this.citaMapper.toCitaEntity(cita);

        if (citaEntity.getRecursosReservados() != null) {
            citaEntity.getRecursosReservados().forEach(recurso -> recurso.setCitaEntity(citaEntity));
        }

        return this.citaMapper.toCita(citaRepository.save(citaEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cita> findById(Long idCita) {
        return this.citaRepository.findById(idCita)
                .map(citaMapper::toCita);
    }

    @Override
    public void deleteById(Long idCita) {
        this.citaRepository.deleteById(idCita);
    }

    @Override
    public List<Cita> findAll() {
        return this.citaMapper.toDomainList(this.citaRepository.findAll());
    }

    @Override
    public boolean existCitaSolapadaParaEmpleado(Long idEmpleado, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return this.citaRepository.existsCitaSolapadaParaEmpleado(idEmpleado, fechaInicio, fechaFin);
    }

    @Override
    public boolean existCitaSolapadaParaRecurso(Long idRecurso, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return this.citaRepository.existsCitaSolapadaParaRecurso(idRecurso, fechaInicio, fechaFin);
    }
}
