package com.salondebelleza.salondebelleza.agendamiento.application.usecase.auth;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.auth.LoginRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.auth.LoginResponse;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Usuario;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.UsuarioNoExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.UsuarioRepositoryPort;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.config.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class LoginUseCase {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Autowired
    public LoginUseCase(AuthenticationManager authenticationManager, UsuarioRepositoryPort usuarioRepositoryPort, JwtService jwtService, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    public LoginResponse execute (LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.nombreUsuario(), request.contrasena()));

        Usuario usuario = this.usuarioRepositoryPort.findByNombreUsuario(request.nombreUsuario())
                .orElseThrow(() -> new UsuarioNoExistenteException("Usuario no encontrado"));

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(usuario.getNombreUsuario());

        boolean requiereMfa = "OWNER".equals(usuario.getRol()) || Boolean.TRUE.equals(usuario.getMfaEnabled());

        if (requiereMfa) {
            String preToken = jwtService.generatePreAuthToken(userDetails);
            return new LoginResponse(preToken, true, "Autenticación multifactor requerida");
        }

        String accessToken = jwtService.generateToken(userDetails);
        return new LoginResponse(accessToken, false, "Inicio de sesión exitoso");
    }
}
