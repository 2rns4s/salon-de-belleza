# Seguridad - Salón de Belleza

![Security](https://img.shields.io/badge/Security-JWT%2BMFA-brightgreen?style=flat-square)

**Última actualización:** Octubre 2026

---

## 📋 Tabla de Contenidos

1. [Autenticación](#autenticación)
2. [Autorización](#autorización)
3. [JWT (JSON Web Tokens)](#jwt-json-web-tokens)
4. [MFA (Multi-Factor Authentication)](#mfa-multi-factor-authentication)
5. [Encriptación](#encriptación)
6. [Validaciones](#validaciones)
7. [Best Practices](#best-practices)

---

## Autenticación

La autenticación es el proceso de verificar **quién eres**.

### Flujo de Autenticación

```
1. Usuario proporciona: nombreUsuario + contraseña
           ↓
2. Backend valida contra tabla `usuarios`
           ↓
3a. Si MFA NO habilitado:
    → Generar JWT
    → Retornar token
           ↓
3b. Si MFA habilitado:
    → Generar pre-auth token (5 min)
    → Usuario debe ingresar código TOTP
    → Retornar token final (1 hora)
```

### Paso 1: Login Inicial

```bash
POST /api/v1/auth/login
Content-Type: application/json

{
  "nombreUsuario": "admin",
  "contrasena": "password123"
}
```

**Validaciones:**
- ✅ Usuario existe en tabla `usuarios`
- ✅ `activo = true`
- ✅ Contraseña coincide (BCrypt)

**Respuesta SIN MFA:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "mfaRequired": false,
  "mensaje": "Login exitoso"
}
```

**Respuesta CON MFA:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "mfaRequired": true,
  "mensaje": "Proporciona código MFA para completar login"
}
```

### Paso 2: Verificación MFA (si aplica)

Solo necesario si `mfaRequired: true`.

```bash
POST /api/v1/auth/verify-mfa
Content-Type: application/json

{
  "nombreUsuario": "admin",
  "totpCode": "123456",
  "preAuthToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Validaciones:**
- ✅ Pre-auth token no expirado (5 min)
- ✅ Código TOTP válido (ventana 30s)

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "mfaRequired": false,
  "mensaje": "MFA verificado - Login completado"
}
```

---

## Autorización

La autorización es el proceso de determinar **qué puedes hacer**.

### Roles

Sistema de dos roles:

| Rol | Permisos |
|-----|----------|
| **OWNER** | ✅ Acceso total a todos los endpoints |
| **RECEPTIONIST** | ✅ Gestión de clientes, servicios, citas, disponibilidad<br>❌ No puede crear/modificar usuarios<br>❌ No puede crear/modificar empleados |

### Protección por Rol

```java
@PostMapping
@PreAuthorize("hasRole('OWNER')")  // Solo OWNER
public ResponseEntity<UsuarioResponse> crearUsuario(...) {
    // ...
}

@GetMapping
@PreAuthorize("hasAnyRole('OWNER', 'RECEPTIONIST')")  // Ambos roles
public ResponseEntity<ClienteResponse> obtenerCliente(...) {
    // ...
}
```

### Flujo de Autorización

```
1. Cliente envía request con Authorization header
   Authorization: Bearer <token>
           ↓
2. JwtAuthenticationFilter intercepta
           ↓
3. JwtService valida token
           ↓
4. Extrae claims: usuarioId, rol
           ↓
5. Spring Security establece contexto
           ↓
6. @PreAuthorize valida rol (si existe)
           ↓
7a. ✅ Rol correcto → Procede
7b. ❌ Rol incorrecto → 403 Forbidden
```

---

## JWT (JSON Web Tokens)

### Estructura

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkFkbWluIiwiaWF0IjoxNTE2MjM5MDIyfQ.
SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

**Partes:**
1. **Header** (algoritmo): `{"alg":"HS256","typ":"JWT"}`
2. **Payload** (claims): `{"sub":"1","nombre":"Admin","iat":1516239022}`
3. **Signature**: Hash con secret key

### Claims

```json
{
  "sub": "1",                           // Subject (usuario ID)
  "nombreUsuario": "admin",             // Username
  "rol": "OWNER",                       // Role
  "iat": 1516239022,                    // Issued at
  "exp": 1516325422,                    // Expiration (1 hora después)
  "iss": "salon-belleza",               // Issuer
  "preAuth": false                      // ¿Es pre-auth? (solo para MFA)
}
```

### Tokens

| Token | Duración | Uso | Pre-Auth |
|-------|----------|-----|----------|
| **Access Token** | 1 hora | Todos los endpoints | false |
| **Pre-Auth Token** | 5 minutos | Solo `/auth/verify-mfa` | true |

### Verificación de Token

```java
@Component
public class JwtService {
    private final String secretKey = "..."; // 256-bit key (32 bytes)
    
    public String generateToken(Usuario usuario) {
        return Jwts.builder()
            .setSubject(usuario.getIdUsuario().toString())
            .claim("nombreUsuario", usuario.getNombreUsuario())
            .claim("rol", usuario.getRol())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1h
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }
    
    public String getUserFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
}
```

### Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| `401 Unauthorized` | Token expirado | Re-login |
| `401 Unauthorized` | Token inválido | Verificar signature |
| `403 Forbidden` | Rol insuficiente | Usar usuario con rol apropiado |
| `400 Bad Request` | Header format incorrecto | `Authorization: Bearer <token>` |

---

## MFA (Multi-Factor Authentication)

### Implementación: TOTP

**TOTP** = Time-based One-Time Password (RFC 6238)

Genera códigos de 6 dígitos válidos por 30 segundos.

### Configuración

#### 1. Habilitar MFA en BD

```sql
UPDATE usuarios 
SET mfa_enable = true,
    mfa_secret = '<secret-key>' 
WHERE id_usuario = 1;
```

#### 2. Compartir Secret con Usuario

El usuario escanea QR code con Google Authenticator:

```
otpauth://totp/SalonDeBelleza:admin@example.com?secret=...&issuer=SalonDeBelleza
```

#### 3. Usuario Ingresa Código

En cada login, proporciona código de 6 dígitos.

### Flujo MFA

```
Usuario: nombreUsuario + contraseña
             ↓
Backend: valida credenciales
             ↓
¿MFA habilitado?
    ├─ NO → Retorna Access Token (1h)
    └─ SÍ → Retorna Pre-Auth Token (5 min)
             ↓
Usuario: pre-auth-token + código TOTP (6 dígitos)
             ↓
Backend: valida código (ventana ±30s)
             ↓
¿Código válido?
    ├─ SÍ → Retorna Access Token (1h)
    └─ NO → 401 Unauthorized (Max 5 intentos)
```

### Setup de MFA (Endpoints Dedicados)

El usuario puede auto-habilitar MFA con los endpoints reales del `MfaController`:

#### 1. GET /api/v1/usuarios/me/mfa/setup
Genera un código QR que el usuario escanea con su app de autenticación.

```bash
curl http://localhost:8080/api/v1/usuarios/me/mfa/setup \
  -H "Authorization: Bearer <token>"
```

**Response:**
```json
{
  "secret": "JBSWY3DPEBLW64TMMQ======",
  "qrCodeUrl": "data:image/png;base64,iVBORw0KGgo..."
}
```

#### 2. POST /api/v1/usuarios/me/mfa/enable
Confirma el setup validando el código TOTP. Guarda `mfaEnabled = true` y `mfaSecret`.

```bash
curl -X POST http://localhost:8080/api/v1/usuarios/me/mfa/enable \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"secret":"<secret-from-setup>","code":"123456"}'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Codificación en Aplicación

```java
// En MfaController
private final GenerarMfaSetupUseCase generarMfaSetupUseCase;
private final HabilitarMfaUseCase habilitarMfaUseCase;

@GetMapping("/setup")
public ResponseEntity<MfaSetupResponse> setupMfa(@AuthenticationPrincipal UserDetails userDetails) {
    return ResponseEntity.ok(generarMfaSetupUseCase.execute(userDetails.getUsername()));
}

@PostMapping("/enable")
public ResponseEntity<AuthResponse> enableMfa(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody MfaSetupRequest request) {
    return ResponseEntity.ok(habilitarMfaUseCase.execute(userDetails.getUsername(), request));
}
```

### Cliente: Generar Código

Google Authenticator, Authy, Microsoft Authenticator, etc.

```
1. Abrir app
2. Escanear QR code
3. App genera código 6 dígitos
4. Código cambia cada 30 segundos
```

### Códigos de Recuperación

Backup codes (formato: 8 caracteres) en caso de perder el teléfono:

```json
{
  "codigos_recuperacion": [
    "ABC123XY",
    "DEF456UV",
    "GHI789ST"
  ]
}
```

Cada código puede usarse 1 sola vez.

---

## Encriptación

### Contraseñas: BCrypt

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// En usuario creation:
usuario.setContrasena(passwordEncoder.encode("password123"));

// En login:
if (passwordEncoder.matches("password123", usuario.getContrasena())) {
    // OK
}
```

**Características:**
- ✅ One-way (no reversible)
- ✅ Salted (protege contra rainbow tables)
- ✅ Slow (defensiva contra brute force)

### JWT Secret Key

256-bit (32 bytes) clave aleatoria:

```
java -cp .../spring-security-crypto.jar org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
```

Guardar en variable de entorno (NO en código):

```bash
export SECURITY_JWT_SECRET_KEY="tu-clave-super-secreta-de-256-bits"
```

### Cifrado de Datos en Reposo (Base de Datos - PII)

Para proteger datos personales sensibles (PII) de los clientes, las columnas `documento` y `telefono` se almacenan cifradas en PostgreSQL utilizando **AES (AES/ECB/PKCS5Padding)** con codificación Base64.

#### Implementación con JPA AttributeConverter

Se utiliza la clase `CryptoConverter` (`@Converter`) que intercepta de forma transparente las operaciones de persistencia y lectura en la base de datos:

```java
@Converter
@Component
public class CryptoConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static byte[] KEY;

    @Value("${security.db.encryption.key}")
    public void setKey(String key) {
        KEY = key.getBytes();
    }

    @Override
    public String convertToDatabaseColumn(String datoEnTextoPlano) {
        // Cifra con AES y retorna Base64
    }

    @Override
    public String convertToEntityAttribute(String datoEnTextoCifrado) {
        // Decodifica Base64 y descifra con AES
    }
}
```

#### Aplicación en Entidades (`ClienteEntity`)

```java
@Convert(converter = CryptoConverter.class)
@Column(unique = true, nullable = false, length = 255)
private String documento;

@Convert(converter = CryptoConverter.class)
@Column(unique = true, nullable = false, length = 255)
private String telefono;
```

#### Migración de Esquema (Flyway V8)

Dado que el texto cifrado resultante en Base64 supera la longitud de los datos en texto plano (ej: 8-10 caracteres), la migración `V8__ampliar_columnas_cifrado_clientes.sql` expande las columnas a `VARCHAR(255)`:

```sql
ALTER TABLE clientes
    ALTER COLUMN documento TYPE VARCHAR(255),
    ALTER COLUMN telefono TYPE VARCHAR(255);
```

#### Clave de Cifrado

Configurable vía `application.properties` o variable de entorno (`SECURITY_DB_ENCRYPTION_KEY`):

```properties
security.db.encryption.key=EstaEsUnaClaveSecretaDe32Bytes!!
```

### HTTPS/TLS

En producción, SIEMPRE usar HTTPS:

```
🔒 Todos los requests: https://api.example.com/...
```

---

## Validaciones

### Validaciones en Entrada

#### 1. Anotaciones JSR-380

```java
@NotNull
@NotBlank
@Email
@Size(min = 8, max = 100)
@Min(1)
@Max(100)
@Pattern(regexp = "^3\\d{9}$")
```

Aplicadas en DTOs:

```java
public class CrearClienteRequest {
    @NotBlank
    @Size(min = 3, max = 100)
    private String nombre;
    
    @NotBlank
    @Email
    private String correo;
    
    @Pattern(regexp = "^3\\d{9}$")
    private String telefono;
}
```

#### 2. Validaciones en UseCase

```java
public class CrearClienteUseCase {
    public ClienteResponse execute(CrearClienteRequest request) {
        // Validaciones de negocio
        if (repository.buscarPorDocumento(request.getDocumento()) != null) {
            throw new ClienteExistenteException();
        }
        
        // Proceder...
    }
}
```

### Validaciones de Negocio (Citas)

```
✅ Empleado existe y está activo
✅ Empleado tiene calificación para servicio
✅ Empleado trabaja en ese día/hora
✅ No hay solapamiento con otras citas del empleado
✅ Recursos disponibles en cantidad suficiente
✅ Fecha no es pasada
✅ Horario dentro de rango del salón
```

### Sanitización

No se hacen (DTOs ya tienen `@NotBlank`, etc.):

```java
// No hacemos:
html = HtmlUtils.htmlEscape(html); // No necesario aquí

// La validación de entrada es suficiente
```

---

## Best Practices

### ✅ DO (Hacer)

- ✅ Usar HTTPS en producción
- ✅ Almacenar JWT secret en variable de entorno
- ✅ Usar HTTPS + HSTS (Strict-Transport-Security)
- ✅ Implementar rate limiting en `/auth/login`
- ✅ Validar entrada en Controller + UseCase
- ✅ Usar soft delete para auditoría
- ✅ Loguear intentos fallidos de login
- ✅ Expirar tokens regularmente
- ✅ Usar roles y @PreAuthorize
- ✅ Sanitizar mensajes de error (no exponer DB internals)

### ❌ DON'T (No hacer)

- ❌ Guardar JWT secret en código
- ❌ Usar HTTP en producción
- ❌ Confiar solo en validación de frontend
- ❌ Almacenar contraseñas en texto plano
- ❌ Retornar contraseñas en responses
- ❌ Tener tokens sin expiración
- ❌ Ignorar validaciones de input
- ❌ Exponer errores técnicos a clientes
- ❌ Guardar información sensible en JWT
- ❌ Permitir usuario admin sin MFA

### Configuración de Producción

```properties
# application-prod.properties

# Seguridad
spring.profiles.active=prod
server.ssl.enabled=true
server.ssl.key-store=${SSL_KEYSTORE_PATH}
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}

# CORS
cors.allowed-origins=https://tudominio.com
cors.allowed-methods=GET,POST,PUT,DELETE
cors.allow-credentials=true

# Headers de seguridad
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.same-site=Strict
```

### Rate Limiting (Recomendado)

```java
// Usando Bucket4j o similar
@PostMapping("/login")
@RateLimiter(name = "auth")
public ResponseEntity<LoginResponse> login(...) {
    // Max 5 intentos por minuto
}
```

---

## Auditoría

### Campos de Auditoría

Todas las tablas tienen:

```sql
fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- fecha_actualizacion TIMESTAMP (opcional)
```

### Soft Delete

```sql
-- En empleados y usuarios
DELETE FROM empleados WHERE id = 1;
-- Realmente se hace:
UPDATE empleados SET activo = false WHERE id = 1;

-- Consultas SIEMPRE deben filtrar:
SELECT * FROM empleados WHERE activo = true;
```

### Logs

```
[INFO] Login exitoso: usuario=admin, ip=192.168.1.1, timestamp=...
[WARN] Fallo login: usuario=admin, intentos=3, ip=192.168.1.1
[ERROR] Acceso denegado: usuario=receptionist, endpoint=/usuarios, rol=RECEPTIONIST
```

---

## Recursos Adicionales

- 🔗 [JWT.io](https://jwt.io) - Debugger de JWT
- 🔗 [OWASP Top 10](https://owasp.org/www-project-top-ten/) - Vulnerabilidades comunes
- 🔗 [RFC 6238 - TOTP](https://tools.ietf.org/html/rfc6238) - Especificación TOTP
- 🔗 [Spring Security Docs](https://spring.io/projects/spring-security) - Documentación oficial

---

**Última actualización:** Octubre 2026 | **Versión:** 0.0.1-SNAPSHOT


