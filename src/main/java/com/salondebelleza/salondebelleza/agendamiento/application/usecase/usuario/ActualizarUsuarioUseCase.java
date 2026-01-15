package com.salondebelleza.salondebelleza.agendamiento.application.usecase.usuario;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.usuario.ActualizarUsuarioRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.usuario.UsuarioResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.UsuarioDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Usuario;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.UsuarioNoExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.UsuarioRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActualizarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final UsuarioDTOMapper usuarioDTOMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ActualizarUsuarioUseCase(UsuarioRepositoryPort usuarioRepositoryPort, UsuarioDTOMapper usuarioDTOMapper, PasswordEncoder passwordEncoder) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.usuarioDTOMapper = usuarioDTOMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse execute(Long idUsuario, ActualizarUsuarioRequest request){

        Usuario usuario = this.usuarioRepositoryPort.findById(idUsuario).orElseThrow(
                () -> new UsuarioNoExistenteException("El usuario con id " + idUsuario + " no existe")
        );

        this.usuarioDTOMapper.updateUsuarioFromDto(request, usuario);

        if (request.contrasena() != null && !request.contrasena().isBlank()) {
            usuario.setContrasena(this.passwordEncoder.encode(request.contrasena()));
        }

        return this.usuarioDTOMapper.toUsuarioResponse(this.usuarioRepositoryPort.save(usuario));
    }
}
