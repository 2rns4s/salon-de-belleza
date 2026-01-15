package com.salondebelleza.salondebelleza.agendamiento.application.usecase.usuario;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.usuario.UsuarioResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.UsuarioDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final UsuarioDTOMapper usuarioDTOMapper;

    public List<UsuarioResponse> execute() {
        return this.usuarioRepositoryPort.findAll()
                .stream()
                .map(usuarioDTOMapper::toUsuarioResponse)
                .toList();
    }
}
