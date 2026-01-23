package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence;

import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Usuario;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.UsuarioRepositoryPort;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.UsuarioEntity;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.mapper.UsuarioMapper;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioPersistenceAdapter implements UsuarioRepositoryPort {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Autowired
    public UsuarioPersistenceAdapter(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public Usuario save(Usuario usuario) {

        UsuarioEntity usuarioEntity = this.usuarioMapper.toUsuarioEntity(usuario);

        if (usuario.getIdUsuario() == null || usuario.getIdUsuario() == 0) {
            usuarioEntity.setIdUsuario(null);
        }

        return this.usuarioMapper.toUsuario(this.usuarioRepository.save(usuarioEntity));
    }


    @Override
    public Optional<Usuario> findById(Long id) {
        return this.usuarioRepository.findById(id)
                .map(usuarioMapper::toUsuario);
    }

    @Override
    public List<Usuario> findAll() {
        return this.usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toUsuario)
                .toList();
    }

    @Override
    public boolean existUsuarioByIdEmpleado(Long idEmpleado) {
        return this.usuarioRepository.existsUsuarioByEmpleadoEntity_IdEmpleado(idEmpleado);
    }

    @Override
    public boolean existUsuarioByNombreUsuario(String nombreUsuario) {
        return this.usuarioRepository.existsUsuarioByNombreUsuario(nombreUsuario);
    }

    @Override
    public Optional<Usuario> findByNombreUsuario(String nombreUsuario) {
        return this.usuarioRepository.findByNombreUsuario(nombreUsuario)
                .map(usuarioMapper::toUsuario);
    }

}
