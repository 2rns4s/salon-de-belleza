package com.salondebelleza.salondebelleza.agendamiento.application.usecase.auth;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.auth.LoginResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.auth.MfaVerificationRequest;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Usuario;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.UsuarioNoExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.UsuarioRepositoryPort;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.config.security.JwtService;
import dev.samstevens.totp.code.CodeVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class VerificarMfaUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final CodeVerifier codeVerifier;

    @Autowired
    public VerificarMfaUseCase(UsuarioRepositoryPort usuarioRepositoryPort, JwtService jwtService, UserDetailsService userDetailsService, CodeVerifier codeVerifier) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.codeVerifier = codeVerifier;
    }

    public LoginResponse execute(MfaVerificationRequest request){

        Usuario usuario = this.usuarioRepositoryPort.findByNombreUsuario(request.nombreUsuario())
                .orElseThrow(() -> new UsuarioNoExistenteException("El usuario " + request.nombreUsuario() + " no existe"));

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(usuario.getNombreUsuario());

        if (!jwtService.isTokenValid(request.preAuthToken(), userDetails)) {
            throw new BadCredentialsException("Token inválido o expirado");
        }

        String tokenType = jwtService.extractTokenType(request.preAuthToken());
        if (!"PRE_AUTH".equals(tokenType)) {
            throw new BadCredentialsException("Tipo de token inválido para verificación MFA");
        }

        if (usuario.getMfaSecret() == null || !codeVerifier.isValidCode(usuario.getMfaSecret(), request.totpCode())) {
            throw new BadCredentialsException("Codigo MFA incorrecto");
        }

        String accessToken = jwtService.generateToken(userDetails);

        return new LoginResponse(accessToken, false, "Verificación exitosa");
    }
}
