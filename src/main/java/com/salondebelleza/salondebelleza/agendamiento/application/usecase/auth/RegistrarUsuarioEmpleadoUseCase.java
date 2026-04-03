package com.salondebelleza.salondebelleza.agendamiento.application.usecase.auth;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.auth.EmpleadoSignUpRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.auth.EmpleadoSignUpResponse;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Empleado;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Usuario;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.EmpleadoConUsuarioYaExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.EmpleadoNoExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.EmpleadoRepositoryPort;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.UsuarioRepositoryPort;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.mapper.EmpleadoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrarUsuarioEmpleadoUseCase {

    private final EmpleadoRepositoryPort empleadoRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_ROL = "RECEPTIONIST";

    @Transactional
    public EmpleadoSignUpResponse execute (EmpleadoSignUpRequest request) {

        Empleado empleado = this.empleadoRepositoryPort.findByCorreo(request.email())
                .orElseThrow(() -> new EmpleadoNoExistenteException("Empleado con correo " + request.email() + " no existe"));

        if (this.usuarioRepositoryPort.existUsuarioByIdEmpleado(empleado.getIdEmpleado())){
            throw  new EmpleadoConUsuarioYaExistenteException("Este correo ya tiene una cuenta de usuario reclamada.");
        }

        if (this.usuarioRepositoryPort.existUsuarioByNombreUsuario(request.username())){
            throw  new EmpleadoConUsuarioYaExistenteException("Este nombre de usuario ya está en uso.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(request.username());
        usuario.setContrasena(this.passwordEncoder.encode(request.password()));
        usuario.setRol(DEFAULT_ROL);
        usuario.setIdEmpleado(empleado.getIdEmpleado());
        usuario.setMfaEnabled(false);
        usuario.setActivo(true);

        Usuario usuarioGuardado = this.usuarioRepositoryPort.save(usuario);

        return new EmpleadoSignUpResponse(
                empleado.getIdEmpleado(),
                usuarioGuardado.getNombreUsuario(),
                usuarioGuardado.getRol()
        );
    }

}
