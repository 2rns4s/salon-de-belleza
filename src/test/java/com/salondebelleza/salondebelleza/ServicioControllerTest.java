package com.salondebelleza.salondebelleza;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.servicio.CrearServicioRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.servicio.RecursoRequerido;
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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class ServicioControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    ServicioControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, JdbcTemplate jdbcTemplate) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM servicios");
        jdbcTemplate.execute("DELETE FROM recursos");
    }

    @Test
    void crearServicio_DeberiaRetornar201_CuandoLosDatosSonValidos() throws Exception {
        this.jdbcTemplate.execute("INSERT INTO recursos VALUES (1, 'silla', 3)");
        RecursoRequerido recursoRequerido = new RecursoRequerido(
                1L,
                1
        );

        List<RecursoRequerido> recursoRequeridos = List.of(recursoRequerido);

        CrearServicioRequest request = new CrearServicioRequest(
                "Corte de cabello",
                60,
                recursoRequeridos
        );

        mockMvc.perform(post("/api/v1/servicios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idServicio").exists())
                .andExpect(jsonPath("$.nombre").value("Corte de cabello"))
                .andExpect(jsonPath("$.duracion").value(60))
                .andExpect(jsonPath("$.recursos[0].idRecurso").value(1));
    }

    @Test
    void crearServicio_DeberiaRetornar409_CuandoHayUnServicioExistente() throws Exception {
        this.jdbcTemplate.execute("INSERT INTO servicios (nombre, duracion) VALUES ('Corte de cabello', 60)");
        this.jdbcTemplate.execute("INSERT INTO recursos VALUES (1, 'silla', 3)");
        CrearServicioRequest nombreDuplicado = new CrearServicioRequest(
                "Corte de cabello",
                60,
                List.of(new RecursoRequerido(
                        1L,
                        1
                ))
        );

        mockMvc.perform(post("/api/v1/servicios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(nombreDuplicado)))
                .andExpect(status().isConflict());
    }
}
