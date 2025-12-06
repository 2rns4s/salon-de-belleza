package com.salondebelleza.salondebelleza;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.recursos.CrearRecursoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class RecursoControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RecursoControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, JdbcTemplate jdbcTemplate) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM recursos");
    }

    @Test
    void crearRecurso_DeberiaRetornar201_CuandoLosDatosSonValidos() throws Exception {
        CrearRecursoRequest request = new CrearRecursoRequest(
                "silla",
                3
        );

        mockMvc.perform(post("/api/v1/recursos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idRecurso").exists())
                .andExpect(jsonPath("$.nombre").value("silla"))
                .andExpect(jsonPath("$.cantidadTotal").value(3));
    }

    @Test
    void crearRecurso_DeberiaRetornar409_CuandoHayUnRecursoExistente() throws Exception {
        this.jdbcTemplate.execute("INSERT INTO recursos (nombre, cantidad_total) VALUES ('silla', 3)");
        CrearRecursoRequest nombreDuplicado = new CrearRecursoRequest(
                "silla",
                3
        );

        mockMvc.perform(post("/api/v1/recursos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(nombreDuplicado)))
                .andExpect(status().isConflict());
    }
}
