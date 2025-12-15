package com.salondebelleza.salondebelleza;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.empleado.CrearEmpleadoRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.empleado.CrearFranjaRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public class EmpleadoControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EmpleadoControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, JdbcTemplate jdbcTemplate) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    private void setUp() {
        jdbcTemplate.execute("DELETE FROM calificaciones");
        jdbcTemplate.execute("DELETE FROM franjas_laborales");
        jdbcTemplate.execute("DELETE FROM empleados");
        jdbcTemplate.execute("DELETE FROM servicios");
    }

    @Test
    void crearEmpleado_DeberiaRetornar201_CuandoLosDatosSonValidos() throws Exception {

        jdbcTemplate.execute("INSERT INTO servicios (id_servicio, nombre, duracion) VALUES (1, 'Corte de cabello', 30)");

        List<CrearFranjaRequest> franjasLaborales = List.of(
                new CrearFranjaRequest(
                        DayOfWeek.MONDAY,
                        LocalTime.of(8, 00),
                        LocalTime.of(12, 00)
                ),
                new CrearFranjaRequest(
                        DayOfWeek.MONDAY,
                        LocalTime.of(13, 00),
                        LocalTime.of(16, 00)
                )
        );

        List<Long> servicios = List.of(1L);

        CrearEmpleadoRequest request = new CrearEmpleadoRequest(
                "John",
                "Doe",
                "John@mail.com",
                "3160012345",
                franjasLaborales,
                servicios
                );

        mockMvc.perform(post("/api/v1/empleados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Validar datos básicos del empleado
                .andExpect(jsonPath("$.idEmpleado").exists())
                .andExpect(jsonPath("$.idEmpleado").isNumber())
                .andExpect(jsonPath("$.nombre").value("John"))
                .andExpect(jsonPath("$.apellido").value("Doe"))
                .andExpect(jsonPath("$.correo").value("John@mail.com"))
                .andExpect(jsonPath("$.telefono").value("3160012345"))
                // Validar estructura de franjas laborales
                .andExpect(jsonPath("$.franjasLaborales").isArray())
                .andExpect(jsonPath("$.franjasLaborales", hasSize(2)))
                .andExpect(jsonPath("$.franjasLaborales[*].idFranjaLaboral").exists())
                // Validar primera franja laboral
                .andExpect(jsonPath("$.franjasLaborales[0].dia").value("MONDAY"))
                .andExpect(jsonPath("$.franjasLaborales[0].horaInicio").value("08:00:00"))
                .andExpect(jsonPath("$.franjasLaborales[0].horaFin").value("12:00:00"))
                // Validar segunda franja laboral
                .andExpect(jsonPath("$.franjasLaborales[1].dia").value("MONDAY"))
                .andExpect(jsonPath("$.franjasLaborales[1].horaInicio").value("13:00:00"))
                .andExpect(jsonPath("$.franjasLaborales[1].horaFin").value("16:00:00"))
                // Validar servicios asignados
                .andExpect(jsonPath("$.serviciosIds").isArray())
                .andExpect(jsonPath("$.serviciosIds", hasSize(1)))
                .andExpect(jsonPath("$.serviciosIds[0]").value(1));
    }

    @Test
    void createEmpleado_DeberiaRetornar409_CuandoHayUnCorreoExistente() throws Exception {
        // Arrange - Insertar un empleado existente y el servicio necesario
        jdbcTemplate.execute("INSERT INTO servicios (id_servicio, nombre, duracion) VALUES (1, 'Corte de cabello', 30)");
        jdbcTemplate.execute("INSERT INTO empleados (nombre, apellido, correo, telefono) VALUES ('Jane', 'Smith', 'jane.smith@mail.com', '3101234567')");

        // Preparar datos para crear un nuevo empleado con el mismo correo
        List<CrearFranjaRequest> franjasLaborales = List.of(
                new CrearFranjaRequest(
                        DayOfWeek.TUESDAY,
                        LocalTime.of(9, 0),
                        LocalTime.of(17, 0)
                )
        );

        List<Long> servicios = List.of(1L);

        CrearEmpleadoRequest requestCorreoDuplicado = new CrearEmpleadoRequest(
                "Another",
                "Person",
                "jane.smith@mail.com", // Mismo correo que el empleado existente
                "3209876543",
                franjasLaborales,
                servicios
        );

        // Act & Assert - Intentar crear empleado con correo duplicado
        mockMvc.perform(post("/api/v1/empleados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(requestCorreoDuplicado)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(containsString("correo")))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
