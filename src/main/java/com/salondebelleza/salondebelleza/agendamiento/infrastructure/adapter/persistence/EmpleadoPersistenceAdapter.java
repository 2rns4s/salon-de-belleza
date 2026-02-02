package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence;

import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Empleado;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.EmpleadoRepositoryPort;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.EmpleadoEntity;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.mapper.EmpleadoMapper;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class EmpleadoPersistenceAdapter implements EmpleadoRepositoryPort {

    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoMapper empleadoMapper;

    @Autowired
    public EmpleadoPersistenceAdapter(EmpleadoRepository empleadoRepository, EmpleadoMapper empleadoMapper) {
        this.empleadoRepository = empleadoRepository;
        this.empleadoMapper = empleadoMapper;
    }


    @Override
    @Transactional
    public Empleado save(Empleado empleado) {
        EmpleadoEntity entity = empleadoMapper.toEmpleadoEntity(empleado);

        EmpleadoEntity savedEntity = empleadoRepository.save(entity);

        return this.empleadoMapper.toEmpleado(savedEntity);
    }

    @Override
    public Optional<Empleado> findById(long idEmpleado) {
        return this.empleadoRepository.findById(idEmpleado)
                .map(empleadoMapper::toEmpleado);
    }

    @Override
    public List<Empleado> findAll() {
        return this.empleadoRepository.findAll()
                .stream()
                .map(empleadoMapper::toEmpleado)
                .toList();
    }

    @Override
    public void deleteById(long idEmpleado) {
        this.empleadoRepository.deleteById(idEmpleado);
    }

    @Override
    public boolean existsByCorreo(String correo) {
        return this.empleadoRepository.existsByCorreo(correo);
    }

    @Override
    public Optional<Empleado> findByIdEmpleadoWithFranjasAndLock(Long idEmpleado) {
        return this.empleadoRepository.findByIdEmpleadoWithFranjasAndLock(idEmpleado)
                .map(empleadoMapper::toEmpleado);
    }

    @Override
    public boolean existByIdEmpleado(Long idEmpleado) {
        return this.empleadoRepository.existsByIdEmpleado(idEmpleado);
    }
}
