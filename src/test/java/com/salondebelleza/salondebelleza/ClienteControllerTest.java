package com.salondebelleza.salondebelleza;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.cliente.CrearClienteRequest;
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
public class ClienteControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ClienteControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, JdbcTemplate jdbcTemplate) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM clientes");
    }

    @Test
    void crearCliente_DeberiaRetornar201_CuandoLosDatosSonValidos() throws Exception {
        CrearClienteRequest request = new CrearClienteRequest(
                "Nombre",
                "Apellido",
                "3001234567",
                "1234567890"
        );

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCliente").exists())
                .andExpect(jsonPath("$.nombre").value("Nombre"))
                .andExpect(jsonPath("$.telefono").value("3001234567"))
                .andExpect(jsonPath("$.documento").value("1234567890"));
    }

    @Test
    void crearCliente_DeberiaRetornar409_CuandoHayUnClienteExistente() throws Exception {
        jdbcTemplate.execute("INSERT INTO clientes (nombre, apellido, telefono, documento) VALUES ('Nombre', 'Apellido', '3001234567', '1234567890')");

        CrearClienteRequest telefonoDuplicado = new CrearClienteRequest(
                "Nombre",
                "Apellido",
                "3001234567",
                "1000000890"
        );

        CrearClienteRequest documentoDuplicado = new CrearClienteRequest(
                "Nombre",
                "Apellido",
                "3009999999",
                "1234567890"
        );

        mockMvc.perform(post("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(telefonoDuplicado)))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(documentoDuplicado)))
                .andExpect(status().isConflict());
    }
}
