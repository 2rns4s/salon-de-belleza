package com.salondebelleza.salondebelleza.agendamiento.infrastructure.config;

import com.salondebelleza.salondebelleza.agendamiento.infrastructure.config.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile("!test")
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configuración CORS personalizada
                .csrf(AbstractHttpConfigurer::disable) // Deshabilitar CSRF (API Stateless)
                .authorizeHttpRequests(auth -> auth
                        // 1. Endpoints Públicos (Login, Registro, MFA)
                        // Asegúrate que coincida con tu AuthController más adelante
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // 2. Swagger / OpenAPI (Opcional, pero recomendado)
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No crear sesiones de servidor
                )
                .authenticationProvider(authenticationProvider)
                // 4. Agregar nuestro filtro JWT antes del filtro de usuario/password por defecto
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Aquí debes poner el puerto exacto desde el que abres tu HTML
        // (Ej: Live Server de VS Code suele usar el 5500 o 5501)
        configuration.setAllowedOrigins(Arrays.asList(
                "http://127.0.0.1:5500",
                "http://localhost:5500",
                "http://localhost:3000" // Por si usas otro puerto más adelante
        ));

        // Permitimos los métodos HTTP que vas a usar en tus endpoints
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Permitimos los headers que enviaremos (como el Content-Type y Authorization)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplicamos esta configuración a todas las rutas de la API (/api/v1/**)
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
