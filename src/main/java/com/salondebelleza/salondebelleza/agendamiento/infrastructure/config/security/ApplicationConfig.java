package com.salondebelleza.salondebelleza.agendamiento.infrastructure.config.security;

import com.salondebelleza.salondebelleza.agendamiento.domain.exception.UsuarioNoExistenteException;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public ApplicationConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> this.usuarioRepository.findByNombreUsuario(username)
                .map(usuarioEntity -> User.builder()
                        .username(usuarioEntity.getNombreUsuario())
                        .password(usuarioEntity.getContrasena())
                        .roles(usuarioEntity.getRol())
                        .disabled(!usuarioEntity.getActivo())
                        .build())
                .orElseThrow(() -> new UsuarioNoExistenteException("El usuario " + username + " no existe"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
