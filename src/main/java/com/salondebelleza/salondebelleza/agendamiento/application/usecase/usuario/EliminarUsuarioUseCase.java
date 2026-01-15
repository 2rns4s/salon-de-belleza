package com.salondebelleza.salondebelleza.agendamiento.application.usecase.usuario;

import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Usuario;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.UsuarioNoExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.UsuarioRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EliminarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    @Autowired
    public EliminarUsuarioUseCase(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    public void execute (long idUsuario){

        Usuario usuario = this.usuarioRepositoryPort.findById(idUsuario).orElseThrow(
                () -> new UsuarioNoExistenteException("El usuario con id " + idUsuario + " no existe")
        );

        if (!Boolean.TRUE.equals(usuario.getActivo())){
            return;
        }

        usuario.setActivo(false);

        this.usuarioRepositoryPort.save(usuario);

    }
}
