package com.salondebelleza.salondebelleza;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.cita.CrearCitaRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class CitaControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;
    private final TestRestTemplate restTemplate;

    @Autowired
    public CitaControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, JdbcTemplate jdbcTemplate, TestRestTemplate restTemplate) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.restTemplate = restTemplate;
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM citas_recursos");
        jdbcTemplate.execute("DELETE FROM citas");
        jdbcTemplate.execute("DELETE FROM franjas_laborales");
        jdbcTemplate.execute("DELETE FROM servicio_recursos");
        jdbcTemplate.execute("DELETE FROM calificaciones");
        jdbcTemplate.execute("DELETE FROM clientes");
        jdbcTemplate.execute("DELETE FROM empleados");
        jdbcTemplate.execute("DELETE FROM servicios");
        jdbcTemplate.execute("DELETE FROM recursos");
    }

    @Test
    void crearCita_DeberiaRetornar201_CuandoLosDatosSonValidos() throws Exception {
        // Arrange - Preparar datos necesarios en la base de datos
        jdbcTemplate.execute("INSERT INTO clientes (id_cliente, nombre, apellido, telefono, documento) VALUES (1, 'Juan', 'Pérez', '3001234567', '1234567890')");
        jdbcTemplate.execute("INSERT INTO empleados (id_empleado, nombre, apellido, correo, telefono) VALUES (1, 'María', 'González', 'maria@mail.com', '3101234567')");
        jdbcTemplate.execute("INSERT INTO servicios (id_servicio, nombre, duracion) VALUES (1, 'Corte de cabello', 60)");
        jdbcTemplate.execute("INSERT INTO recursos (id_recurso, nombre, cantidad_total) VALUES (1, 'Silla', 5)");
        jdbcTemplate.execute("INSERT INTO servicio_recursos (id_servicio, id_recurso, cantidad_requerida) VALUES (1, 1, 1)");
        jdbcTemplate.execute("INSERT INTO calificaciones (id_empleado, id_servicio) VALUES (1, 1)");
        jdbcTemplate.execute("INSERT INTO franjas_laborales (id_empleado, dia, hora_inicio, hora_fin) VALUES (1, 'MONDAY', '08:00:00', '18:00:00')");

        // Calcular la próxima fecha de lunes a las 10:00 AM
        LocalDateTime fechaInicio = LocalDateTime.now().plusDays(1);
        while (fechaInicio.getDayOfWeek() != java.time.DayOfWeek.MONDAY) {
            fechaInicio = fechaInicio.plusDays(1);
        }
        fechaInicio = fechaInicio.withHour(10).withMinute(0).withSecond(0).withNano(0);

        CrearCitaRequest request = new CrearCitaRequest(
                1L, // idCliente
                1L, // idServicio
                1L, // idEmpleado
                fechaInicio
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Validar datos básicos de la cita
                .andExpect(jsonPath("$.idCita").exists())
                .andExpect(jsonPath("$.idCita").isNumber())
                .andExpect(jsonPath("$.idCliente").value(1))
                .andExpect(jsonPath("$.idServicio").value(1))
                .andExpect(jsonPath("$.idEmpleado").value(1))
                // Validar fechas
                .andExpect(jsonPath("$.fechaInicio").exists())
                .andExpect(jsonPath("$.fechaFin").exists())
                // Validar estado
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"))
                // Validar que se asignaron recursos
                .andExpect(jsonPath("$.recursosAsignados").isArray())
                .andExpect(jsonPath("$.recursosAsignados", hasSize(greaterThan(0))));
    }

    @Test
    void crearCita_DeberiaRetornar409_CuandoHayUnSolapamiento() throws Exception {
        // Arrange - Preparar datos necesarios en la base de datos
        jdbcTemplate.execute("INSERT INTO clientes (id_cliente, nombre, apellido, telefono, documento) VALUES (1, 'Juan', 'Pérez', '3001234567', '1234567890')");
        jdbcTemplate.execute("INSERT INTO clientes (id_cliente, nombre, apellido, telefono, documento) VALUES (2, 'Ana', 'López', '3009876543', '9876543210')");
        jdbcTemplate.execute("INSERT INTO empleados (id_empleado, nombre, apellido, correo, telefono) VALUES (1, 'María', 'González', 'maria@mail.com', '3101234567')");
        jdbcTemplate.execute("INSERT INTO servicios (id_servicio, nombre, duracion) VALUES (1, 'Corte de cabello', 60)");
        jdbcTemplate.execute("INSERT INTO recursos (id_recurso, nombre, cantidad_total) VALUES (1, 'Silla', 5)");
        jdbcTemplate.execute("INSERT INTO servicio_recursos (id_servicio, id_recurso, cantidad_requerida) VALUES (1, 1, 1)");
        jdbcTemplate.execute("INSERT INTO calificaciones (id_empleado, id_servicio) VALUES (1, 1)");
        jdbcTemplate.execute("INSERT INTO franjas_laborales (id_empleado, dia, hora_inicio, hora_fin) VALUES (1, 'MONDAY', '08:00:00', '18:00:00')");

        // Calcular la próxima fecha de lunes a las 10:00 AM
        LocalDateTime fechaInicioExistente = LocalDateTime.now().plusDays(1);
        while (fechaInicioExistente.getDayOfWeek() != java.time.DayOfWeek.MONDAY) {
            fechaInicioExistente = fechaInicioExistente.plusDays(1);
        }
        fechaInicioExistente = fechaInicioExistente.withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fechaFinExistente = fechaInicioExistente.plusMinutes(60);

        jdbcTemplate.execute(String.format(
                "INSERT INTO citas (id_cliente, id_empleado, id_servicio, fecha_inicio, fecha_fin, estado) VALUES (1, 1, 1, '%s', '%s', 'CONFIRMADA')",
                fechaInicioExistente, fechaFinExistente
        ));

        // Intentar crear otra cita en el mismo horario para el mismo empleado
        CrearCitaRequest requestSolapada = new CrearCitaRequest(
                2L, // Otro cliente
                1L, // Mismo servicio
                1L, // Mismo empleado
                fechaInicioExistente.plusMinutes(30) // Horario que se solapa (30 minutos después)
        );

        // Act & Assert - Intentar crear cita con solapamiento
        mockMvc.perform(post("/api/v1/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestSolapada)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(containsString("disponible")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void debeEvitarSolapamientoConBloqueoPesimista_EndToEnd() throws InterruptedException {
        // Insertamos datos base (Igual que hicimos antes)
        jdbcTemplate.execute("INSERT INTO clientes (id_cliente, nombre, apellido, telefono, documento) VALUES (1, 'Test', 'User', '3001', '111')");
        jdbcTemplate.execute("INSERT INTO clientes (id_cliente, nombre, apellido, telefono, documento) VALUES (2, 'Test', 'User2', '3002', '222')");
        jdbcTemplate.execute("INSERT INTO empleados (id_empleado, nombre, apellido, correo, telefono) VALUES (1, 'Estilista', 'Veloz', 'est@mail.com', '333')");
        jdbcTemplate.execute("INSERT INTO servicios (id_servicio, nombre, duracion) VALUES (1, 'Corte Rápido', 30)");
        jdbcTemplate.execute("INSERT INTO recursos (id_recurso, nombre, cantidad_total) VALUES (1, 'Silla Única', 1)"); // CLAVE: Solo 1 silla
        jdbcTemplate.execute("INSERT INTO servicio_recursos (id_servicio, id_recurso, cantidad_requerida) VALUES (1, 1, 1)");
        jdbcTemplate.execute("INSERT INTO calificaciones (id_empleado, id_servicio) VALUES (1, 1)");
        jdbcTemplate.execute("INSERT INTO franjas_laborales (id_empleado, dia, hora_inicio, hora_fin) VALUES (1, 'MONDAY', '08:00:00', '18:00:00')");

        // Calculamos fecha válida (Lunes 10 AM)
        LocalDateTime fechaCita = LocalDateTime.now().plusDays(1);
        while (fechaCita.getDayOfWeek() != java.time.DayOfWeek.MONDAY) { fechaCita = fechaCita.plusDays(1); }
        fechaCita = fechaCita.withHour(10).withMinute(0).withSecond(0).withNano(0);

        // Preparamos DOS requests diferentes (distinto cliente, mismo empleado/recurso/hora)
        var requestDto1 = new CrearCitaRequest(1L, 1L, 1L, fechaCita); // Cliente 1
        var requestDto2 = new CrearCitaRequest(2L, 1L, 1L, fechaCita); // Cliente 2

        // --- 2. ACT (EJECUCIÓN CONCURRENTE) ---
        int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        // Tarea genérica que recibe un request
        Consumer<CrearCitaRequest> enviarPeticion = (request) -> {
            try {
                latch.await(); // Esperar disparo

                ResponseEntity<Object> response = restTemplate.postForEntity(
                        "/api/v1/citas", // <--- OJO: Verifica que este sea tu path correcto
                        request,
                        Object.class
                );

                if (response.getStatusCode().is2xxSuccessful()) {
                    successCount.incrementAndGet();
                } else if (response.getStatusCode().is4xxClientError()) { // Esperamos 409
                    errorCount.incrementAndGet();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // Lanzamos hilos
        CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> enviarPeticion.accept(requestDto1), executorService);
        CompletableFuture<Void> task2 = CompletableFuture.runAsync(() -> enviarPeticion.accept(requestDto2), executorService);

        // ¡DISPARO!
        latch.countDown();

        // Esperar resultados
        CompletableFuture.allOf(task1, task2).join();

        // --- 3. ASSERT (VERIFICACIÓN) ---
        // Verificamos en consola qué pasó
        System.out.println("Éxitos (201): " + successCount.get());
        System.out.println("Fallos (409): " + errorCount.get());

        assertThat(successCount.get()).as("Solo una cita debe crearse").isEqualTo(1);
        assertThat(errorCount.get()).as("La segunda solicitud debe ser rechazada").isEqualTo(1);
    }
}
