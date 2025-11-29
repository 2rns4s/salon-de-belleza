package com.salondebelleza.salondebelleza.agendamiento.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Salon de belleza")
                .version("0.0.1-SNAPSHOT")
                .description("API REST para el salon de belleza")
                .contact(new Contact().name("daniel diaz").email("danielstebandiazosorio@gmail.com")));
    }
}
