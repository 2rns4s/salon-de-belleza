package com.salondebelleza.salondebelleza.agendamiento.application.usecase.usuario;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.usuario.UsuarioResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.UsuarioDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.UsuarioNoExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.UsuarioRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuscarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final UsuarioDTOMapper usuarioDTOMapper;

    @Autowired
    public BuscarUsuarioUseCase(UsuarioRepositoryPort usuarioRepositoryPort, UsuarioDTOMapper usuarioDTOMapper) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.usuarioDTOMapper = usuarioDTOMapper;
    }

    @Transactional(readOnly = true)
    public UsuarioResponse execute(long idUsuario) {
        return this.usuarioDTOMapper.toUsuarioResponse(this.usuarioRepositoryPort.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNoExistenteException("El usuario con id " + idUsuario + " no existe")));
    }
}
