package com.salondebelleza.salondebelleza.agendamiento.application.usecase.usuario;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.usuario.CrearUsuarioRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.usuario.UsuarioResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.UsuarioDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.EmpleadoNoExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.UsuarioExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.EmpleadoRepositoryPort;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.UsuarioRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CrearUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final UsuarioDTOMapper usuarioDTOMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmpleadoRepositoryPort empleadoRepositoryPort;

    @Autowired
    public CrearUsuarioUseCase(UsuarioRepositoryPort usuarioRepositoryPort, UsuarioDTOMapper usuarioDTOMapper, PasswordEncoder passwordEncoder, EmpleadoRepositoryPort empleadoRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.usuarioDTOMapper = usuarioDTOMapper;
        this.passwordEncoder = passwordEncoder;
        this.empleadoRepositoryPort = empleadoRepositoryPort;
    }

    @Transactional
    public UsuarioResponse execute(CrearUsuarioRequest request){
        validarReglasDeNegocio(request);

        var usuario = this.usuarioDTOMapper.toUsuario(request);
        usuario.setContrasena(this.passwordEncoder.encode(request.contrasena()));

        return this.usuarioDTOMapper.toUsuarioResponse(this.usuarioRepositoryPort.save(usuario));
    }

    private void validarReglasDeNegocio(CrearUsuarioRequest request){

        if (this.usuarioRepositoryPort.existUsuarioByNombreUsuario(request.nombreUsuario())){
            throw new UsuarioExistenteException("El usuario " + request.nombreUsuario() + " ya existe");
        }

        if (!this.empleadoRepositoryPort.existByIdEmpleado(request.idEmpleado())){
            throw new EmpleadoNoExistenteException("El empleado con id " + request.idEmpleado() + " no existe");
        }

        if (this.usuarioRepositoryPort.existUsuarioByIdEmpleado(request.idEmpleado())){
            throw new UsuarioExistenteException("El empleado ya tiene un usuario registrado");
        }
    }
}
