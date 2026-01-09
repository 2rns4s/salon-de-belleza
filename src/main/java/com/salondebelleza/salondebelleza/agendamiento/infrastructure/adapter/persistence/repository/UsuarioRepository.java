package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.repository;

import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.data.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByNombreUsuario(String nombreUsuario);
}
