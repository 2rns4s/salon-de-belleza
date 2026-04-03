# Testing - Salón de Belleza

![JUnit5](https://img.shields.io/badge/JUnit-5-green?style=flat-square) 
![Testcontainers](https://img.shields.io/badge/Integration-Testcontainers-blue?style=flat-square)

**Última actualización:** Abril 2026

---

## 📋 Tabla de Contenidos

1. [Estrategia de Testing](#estrategia-de-testing)
2. [Unit Tests](#unit-tests)
3. [Integration Tests](#integration-tests)
4. [Test Coverage](#test-coverage)
5. [Ejecución](#ejecución)
6. [Best Practices](#best-practices)

---

## Estrategia de Testing

### Pirámide de Tests

```
      /\
     /  \  E2E (5%)
    /────\
   /      \  Integration (15%)
  /────────\
 /          \  Unit (80%)
/____________\
```

- **Unit Tests (80%):** Lógica de negocio pura, sin dependencias
- **Integration Tests (15%):** Flujo completo con BD real (Testcontainers)
- **E2E Tests (5%):** Opcional, en CI/CD

### Cobertura Objetivo

- **Mínimo 80%** en código nuevo
- **UseCases:** 95% (lógica crítica)
- **Controllers:** 70% (validación)
- **Entities:** 60% (getters/setters)

### Tipos de Pruebas

| Tipo | Dónde | Qué | Herramienta |
|------|-------|-----|-----------|
| **Unit** | UseCase, Service | Lógica pura | JUnit 5, Mockito |
| **Integration** | Controller, Repository | Flujo real | JUnit 5, Testcontainers, Spring Boot Test |
| **Contract** | API | Requests/Responses | REST Assured |
| **Performance** | Endpoints | Carga | JMeter (opcional) |
| **Security** | Auth, Endpoints | Autenticación | Spring Security Test |

---

## Unit Tests

### Estructura

```java
class <NombreClaseTest> {
    
    @Mock
    private Dependencia1 dependencia1;
    
    @InjectMocks
    private ClaseATestear claseATestear;
    
    @Test
    void nombreDescriptivoDelTest() {
        // ARRANGE - Preparar datos
        // ACT - Ejecutar
        // ASSERT - Verificar
    }
}
```

### Ejemplo: CrearClienteUseCase

```java
@ExtendWith(MockitoExtension.class)
class CrearClienteUseCaseTest {
    
    @Mock
    private ClienteRepositoryPort repository;
    
    @Mock
    private ClienteMapper mapper;
    
    @InjectMocks
    private CrearClienteUseCase useCase;
    
    // ✅ TEST: Caso exitoso
    @Test
    void debeCrearClienteExitosamente() {
        // ARRANGE
        CrearClienteRequest request = new CrearClienteRequest(
            "Juan", "Pérez", "12345678", "3005551234"
        );
        Cliente clienteEsperado = new Cliente(1L, "Juan", "Pérez", "12345678", "3005551234");
        ClienteResponse responseEsperada = new ClienteResponse(1L, "Juan", "Pérez", "12345678", "3005551234");
        
        when(repository.buscarPorDocumento("12345678")).thenReturn(null);
        when(repository.guardar(any(Cliente.class))).thenReturn(clienteEsperado);
        when(mapper.toResponse(clienteEsperado)).thenReturn(responseEsperada);
        
        // ACT
        ClienteResponse response = useCase.execute(request);
        
        // ASSERT
        assertNotNull(response);
        assertEquals("Juan", response.getNombre());
        assertEquals("12345678", response.getDocumento());
        verify(repository).guardar(any(Cliente.class));
    }
    
    // ✅ TEST: Documento duplicado lanza excepción
    @Test
    void debeThrowClienteExistenteException_CuandoDocumentoDuplicado() {
        // ARRANGE
        CrearClienteRequest request = new CrearClienteRequest(
            "Juan", "Pérez", "12345678", "3005551234"
        );
        Cliente clienteExistente = new Cliente(2L, "Otro", "Nombre", "12345678", "3001234567");
        
        when(repository.buscarPorDocumento("12345678")).thenReturn(clienteExistente);
        
        // ACT & ASSERT
        assertThrows(ClienteExistenteException.class, () -> {
            useCase.execute(request);
        });
        
        // Verificar que NO se guardó
        verify(repository, never()).guardar(any());
    }
    
    // ✅ TEST: Validaciones de entrada
    @Test
    void debeValidarNombreNoVacio() {
        CrearClienteRequest request = new CrearClienteRequest(
            "", "Pérez", "12345678", "3005551234"
        );
        // En Controller/DTO, @NotBlank previene esto
        // Aquí podemos testear UseCase recibe null
    }
}
```

### Patrones Comunes

**Mock + When/Then:**
```java
when(repository.buscarPorId(1L)).thenReturn(cliente);
when(repository.buscarPorId(999L)).thenReturn(null);
```

**Verify:**
```java
verify(repository).guardar(any(Cliente.class)); // Se llamó
verify(repository, never()).eliminar(any());    // NO se llamó
verify(repository, times(2)).guardar(any());    // Se llamó 2 veces
```

**ArgumentCaptor:**
```java
ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
verify(repository).guardar(captor.capture());
Cliente clienteGuardado = captor.getValue();
assertEquals("Juan", clienteGuardado.getNombre());
```

---

## Integration Tests

### Setup con Testcontainers

```java
@SpringBootTest
@Testcontainers
class CrearClienteIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
        .withDatabaseName("test_db")
        .withUsername("test_user")
        .withPassword("test_password");
    
    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private ClienteRepository repository;
    
    // Tests...
}
```

### Ejemplo: Controller Integration Test

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ClienteControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");
    
    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    // ✅ TEST: Crear cliente via HTTP
    @Test
    void debeCrearClienteViaRest() {
        // ARRANGE
        CrearClienteRequest request = new CrearClienteRequest(
            "Juan", "Pérez", "12345678", "3005551234"
        );
        
        // ACT
        ResponseEntity<ClienteResponse> response = restTemplate.postForEntity(
            "/api/v1/clientes",
            request,
            ClienteResponse.class
        );
        
        // ASSERT
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Juan", response.getBody().getNombre());
    }
    
    // ✅ TEST: Validación falla
    @Test
    void debeRetornar400_CuandoDatosInvalidos() {
        CrearClienteRequest request = new CrearClienteRequest(
            "", "Pérez", "12345678", "3005551234"  // Nombre vacío
        );
        
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
            "/api/v1/clientes",
            request,
            ErrorResponse.class
        );
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("nombre"));
    }
    
    // ✅ TEST: Documento duplicado
    @Test
    void debeRetornar409_CuandoDocumentoDuplicado() {
        CrearClienteRequest request1 = new CrearClienteRequest(
            "Juan", "Pérez", "12345678", "3005551234"
        );
        CrearClienteRequest request2 = new CrearClienteRequest(
            "Otro", "Nombre", "12345678", "3001234567"  // Mismo documento
        );
        
        // Primer cliente - OK
        restTemplate.postForEntity("/api/v1/clientes", request1, ClienteResponse.class);
        
        // Segundo cliente - Falla
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
            "/api/v1/clientes",
            request2,
            ErrorResponse.class
        );
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
}
```

### Database Setup para Tests

```java
@SpringBootTest
@Testcontainers
@Transactional  // Rollback después de cada test
class ClienteRepositoryTest {
    
    @Autowired
    private ClienteRepository repository;
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");
    
    @BeforeEach
    void setup() {
        // Limpiar datos si es necesario
        repository.deleteAll();
    }
    
    @Test
    void debeGuardarClienteEnBaseDatos() {
        ClienteData cliente = new ClienteData(null, "Juan", "Pérez", "12345678", "3005551234", null);
        
        ClienteData guardado = repository.save(cliente);
        
        assertNotNull(guardado.getId());
        assertEquals("Juan", guardado.getNombre());
    }
}
```

---

## Test Coverage

### Generar Reporte de Cobertura

```bash
# Ejecutar tests con cobertura
./gradlew test jacocoTestReport

# Reporte en:
# build/reports/jacoco/test/html/index.html
```

### Verificar Mínimo de Cobertura

```groovy
// build.gradle
jacocoTestCoverageVerification {
    violationRules {
        rule {
            element = 'CLASS'
            includes = ['com.salondebelleza.salondebelleza.agendamiento.application.usecase.*']
            
            limit {
                minimum = 0.80
            }
        }
    }
}
```

---

## Ejecución

### Línea de Comandos

```bash
# Todos los tests
./gradlew test

# Solo unit tests
./gradlew test --tests "*UseCaseTest"

# Solo integration tests
./gradlew test --tests "*IntegrationTest"

# Test específico
./gradlew test --tests ClienteControllerIntegrationTest.debeCrearClienteViaRest

# Con output detallado
./gradlew test --info

# Detener en primer fallo
./gradlew test --fail-fast
```

### IDE (IntelliJ / VS Code)

- Click derecho en clase → Run Tests
- Click derecho en método → Run Test
- Green checkmark = Todos pasan
- Red X = Falla

### CI/CD (GitHub Actions)

```yaml
# .github/workflows/tests.yml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:17-alpine
        env:
          POSTGRES_DB: test_db
          POSTGRES_USER: test_user
          POSTGRES_PASSWORD: test_password
    
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '21'
      - run: ./gradlew test
      - run: ./gradlew jacocoTestReport
```

---

## Best Practices

### ✅ Nombres Descriptivos

```java
// ✅ BUENO
void debeThrowClienteExistenteException_CuandoDocumentoDuplicado() { }
void debeRetornar404_CuandoClienteNoExiste() { }

// ❌ MALO
void test1() { }
void testCreate() { }
```

### ✅ AAA Pattern (Arrange, Act, Assert)

```java
@Test
void ejemplo() {
    // ARRANGE - Preparar
    CrearClienteRequest request = ...;
    
    // ACT - Ejecutar
    ClienteResponse response = useCase.execute(request);
    
    // ASSERT - Verificar
    assertNotNull(response);
}
```

### ✅ Un concepto por test

```java
// ✅ BUENO - Test único por comportamiento
@Test
void debeValidarNombreNoVacio() { }

@Test
void debeValidarDocumento() { }

// ❌ MALO - Múltiples aserciones
@Test
void testCrearCliente() {
    // Valida nombre
    // Valida documento
    // Valida teléfono
}
```

### ✅ Mocks vs Real

```java
// ✅ Mock para aislamiento
@Mock
private ClienteRepositoryPort repository;

// ✅ Real en Integration Tests
@Autowired
private ClienteRepository repository;
```

### ✅ Test data builders

```java
class CrearClienteRequestBuilder {
    public static CrearClienteRequest defaultRequest() {
        return new CrearClienteRequest("Juan", "Pérez", "12345678", "3005551234");
    }
    
    public static CrearClienteRequest withNombre(String nombre) {
        return new CrearClienteRequest(nombre, "Pérez", "12345678", "3005551234");
    }
}

// Uso:
CrearClienteRequest request = CrearClienteRequestBuilder.defaultRequest();
```

### ❌ Evitar

- ❌ Tests que dependen de orden
- ❌ Tests que dependen de hora/fecha real
- ❌ Tests que no se pueden reproducir
- ❌ Assert genéricos sin contexto
- ❌ Tests lentos (> 1 segundo)

---

## Recursos

- 📚 [JUnit 5 Docs](https://junit.org/junit5/docs/current/user-guide/)
- 📚 [Mockito](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- 📚 [Testcontainers](https://www.testcontainers.org/)
- 📚 [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)

---

**Última actualización:** Abril 2026 | **Versión:** 0.0.1-SNAPSHOT


