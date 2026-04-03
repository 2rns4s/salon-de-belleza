package com.salondebelleza.salondebelleza.agendamiento.application.usecase.auth;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.auth.AuthResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.auth.MfaSetupRequest;
import com.salondebelleza.salondebelleza.agendamiento.domain.entities.Usuario;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.MfaInvalidoException;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.UsuarioNoExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.UsuarioRepositoryPort;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.config.security.JwtService;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.config.security.SecurityUser;
import dev.samstevens.totp.code.CodeVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HabilitarMfaUseCase {

    private final CodeVerifier codeVerifier;
    private final UsuarioRepositoryPort usuarioRepository;
    private final StringRedisTemplate redisTemplate;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse execute (String username, MfaSetupRequest request) {

        String redisKey = "mfa_setup:" + username;

        String tempSecret = redisTemplate.opsForValue().get(redisKey);

        if (tempSecret == null ) {
            throw new MfaInvalidoException("El tiempo para configurar el MFA ha expirado.");
        }

        boolean isValid = codeVerifier.isValidCode(tempSecret, request.code());

        if (!isValid) {
            throw new MfaInvalidoException("Código MFA inválido. Por favor, inténtalo de nuevo.");
        }

        Usuario usuario = usuarioRepository.findByNombreUsuario(username)
                .orElseThrow(() ->  new UsuarioNoExistenteException("Usuario no encontrado: " + username));

        usuario.setMfaEnabled(true);
        usuario.setMfaSecret(tempSecret);
        usuarioRepository.save(usuario);

        redisTemplate.delete(redisKey);

        UserDetails userDetails = new SecurityUser(usuario);

        String newToken = jwtService.generateToken(userDetails);

        return new AuthResponse(newToken);

    }
}
