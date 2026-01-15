package com.salondebelleza.salondebelleza.agendamiento.domain.port;

import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositoryPort {

    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Long id);
    List<Usuario> findAll();
    boolean existUsuarioByIdEmpleado(Long idEmpleado);
    boolean existUsuarioByNombreUsuario(String nombreUsuario);
}
