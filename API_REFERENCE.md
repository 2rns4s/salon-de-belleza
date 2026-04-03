# API Reference - Salón de Belleza

![API Version](https://img.shields.io/badge/API%20Version-v1-blue?style=flat-square) 
![Status](https://img.shields.io/badge/Status-Stable-green?style=flat-square)

**Base URL:** `http://localhost:8080/api/v1`  
**Swagger UI:** `http://localhost:8080/swagger-ui.html`  
**OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

**Última actualización:** Abril 2026

---

## 📋 Tabla de Contenidos

1. [Autenticación](#autenticación)
2. [Formato de Respuestas](#formato-de-respuestas)
3. [Códigos HTTP](#códigos-http)
4. [Controllers & Endpoints](#controllers--endpoints)
5. [DTOs Detallados](#dtos-detallados)
6. [Ejemplos de Uso](#ejemplos-de-uso)
7. [Manejo de Errores](#manejo-de-errores)

---

## Autenticación

### Estrategia: JWT + MFA (TOTP)

Todos los endpoints **EXCEPTO** `/auth/login`, `/auth/verify-mfa` y `/auth/employee-signup` requieren autenticación.

#### Paso 1: Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "nombreUsuario": "admin",
    "contrasena": "password123"
  }'
```

**Respuesta 200 (sin MFA):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "mfaRequired": false,
  "mensaje": "Login exitoso"
}
```

**Respuesta 200 (con MFA habilitado):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "mfaRequired": true,
  "mensaje": "Proporciona código MFA para completar login"
}
```

#### Paso 2: Verificar MFA (si aplica)

Si `mfaRequired: true`, llamar a `/verify-mfa`:

```bash
curl -X POST http://localhost:8080/api/v1/auth/verify-mfa \
  -H "Content-Type: application/json" \
  -d '{
    "nombreUsuario": "admin",
    "totpCode": "123456",
    "preAuthToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'
```

**Respuesta 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "mfaRequired": false,
  "mensaje": "MFA verificado - Login completado"
}
```

#### Paso 3: Usar Token en Requests

Incluir en header `Authorization`:

```bash
curl -X GET http://localhost:8080/api/v1/clientes \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Tokens

| Token | Duración | Uso |
|-------|----------|-----|
| **Pre-Auth** | 5 minutos | Solo para endpoint `/auth/verify-mfa` |
| **Access** | 1 hora | Todos los endpoints protegidos |

---

## Formato de Respuestas

### Respuesta Exitosa

```json
{
  "idCliente": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "documento": "12345678",
  "telefono": "3005551234"
}
```

### Respuesta con Error

Siempre usa estructura `ErrorResponse`:

```json
{
  "timestamp": "2026-04-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error en los datos enviados",
  "details": [
    "nombre: no debe estar vacío",
    "documento: formato inválido"
  ]
}
```

#### Campos de ErrorResponse

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `timestamp` | `LocalDateTime` | Fecha/hora del error |
| `status` | `int` | Código HTTP (400, 401, 404, 500, etc.) |
| `error` | `string` | Texto HTTP estándar (Bad Request, Unauthorized, etc.) |
| `message` | `string` | Mensaje descriptivo del error |
| `details` | `string[]` | Array opcional con detalles (validaciones, etc.) |

---

## Códigos HTTP

| Código | Significado | Ejemplo |
|--------|-------------|---------|
| **200** | ✅ OK | GET exitoso, PUT exitoso |
| **201** | ✅ Created | POST exitoso |
| **204** | ✅ No Content | DELETE exitoso |
| **400** | ❌ Bad Request | Validación falla, datos inválidos |
| **401** | ❌ Unauthorized | Token expirado, credenciales inválidas |
| **403** | ❌ Forbidden | Permisos insuficientes (ej: RECEPTIONIST no puede crear usuarios) |
| **404** | ❌ Not Found | Recurso no existe (cliente, cita, etc.) |
| **409** | ❌ Conflict | Conflicto de negocio (duplicado, solapamiento, etc.) |
| **500** | ❌ Server Error | Error interno no esperado |

---

## Controllers & Endpoints

### 1️⃣ AuthController (`/auth`)

**Públicos (sin JWT)**

#### `POST /api/v1/auth/login`

Inicia autenticación por usuario/contraseña.

**Request:**
```json
{
  "nombreUsuario": "string",
  "contrasena": "string"
}
```

**Response 200:**
```json
{
  "token": "string (JWT)",
  "mfaRequired": "boolean",
  "mensaje": "string"
}
```

**Errores:** 400, 401, 500

---

#### `POST /api/v1/auth/verify-mfa`

Valida código TOTP y completa login si MFA está habilitado.

**Request:**
```json
{
  "nombreUsuario": "string",
  "totpCode": "string (6 dígitos, regex: ^\\d{6}$)",
  "preAuthToken": "string (token pre-auth)"
}
```

**Response 200:**
```json
{
  "token": "string (JWT final)",
  "mfaRequired": false,
  "mensaje": "MFA verificado - Login completado"
}
```

**Errores:** 400, 401, 500

---

#### `POST /api/v1/auth/employee-signup`

Registro público para que un empleado pre-registrado cree su cuenta de usuario.

**Request:**
```json
{
  "email": "string (email)",
  "username": "string (4-20, único)",
  "password": "string (mínimo 8)"
}
```

**Response 201:**
```json
{
  "idEmpleado": 5,
  "nombreUsuario": "maria_lopez",
  "rol": "RECEPTIONIST"
}
```

**Errores:** 400, 404, 409, 500

---

### 1.1️⃣ MfaController (`/usuarios/me/mfa`)

**Protegido (requieren JWT)**

#### `GET /api/v1/usuarios/me/mfa/setup`

Inicia el enrolamiento MFA para el usuario autenticado. Retorna QR code y secret TOTP.

**Request:** Sin body, usa token JWT  
**Response 200:**
```json
{
  "secret": "string (secreto TOTP Base32)",
  "qrCodeUrl": "string (data:image/png;base64,...)"
}
```

**Errores:** 403, 500

---

#### `POST /api/v1/usuarios/me/mfa/enable`

Confirma y habilita MFA validando el código TOTP. Guarda `mfaEnabled = true` y `mfaSecret`.

**Request:**
```json
{
  "secret": "string (recibido de /setup)",
  "code": "string (6 dígitos, regex: ^\\d{6}$)"
}
```

**Response 200:**
```json
{
  "token": "string (nuevo JWT con MFA habilitado)"
}
```

**Errores:** 400, 403, 404, 500

---

### 2️⃣ ClienteController (`/clientes`)

**Protegidos (requieren JWT)**

#### `GET /api/v1/clientes`

Lista todos los clientes.

**Request:** Sin body  
**Response 200:**
```json
[
  {
    "idCliente": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "documento": "12345678",
    "telefono": "3005551234"
  }
]
```

**Errores:** 500

---

#### `GET /api/v1/clientes/{id}`

Obtiene cliente por ID.

**Request:** ID en path  
**Response 200:**
```json
{
  "idCliente": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "documento": "12345678",
  "telefono": "3005551234"
}
```

**Errores:** 404, 500

---

#### `GET /api/v1/clientes/buscar`

Busca por teléfono, documento o nombre.

**Query Parameters (al menos uno obligatorio):**
- `telefono` (string) - Búsqueda exacta
- `documento` (string) - Búsqueda exacta
- `nombre` (string) - Búsqueda parcial (retorna array)

**Response 200:**
```json
{
  "idCliente": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "documento": "12345678",
  "telefono": "3005551234"
}
```

**Errores:** 400 (sin parámetros), 404 (no encontrado), 500

---

#### `POST /api/v1/clientes`

Crea nuevo cliente.

**Request:**
```json
{
  "nombre": "string (3-100 caracteres)",
  "apellido": "string (3-100 caracteres)",
  "telefono": "string (regex: ^3\\d{9}$)",
  "documento": "string (regex: ^\\d{8,10}$)"
}
```

**Response 201:**
```json
{
  "idCliente": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "documento": "12345678",
  "telefono": "3005551234"
}
```

**Errores:** 400, 409 (documento duplicado), 500

---

#### `PUT /api/v1/clientes/{id}`

Actualiza cliente existente.

**Request:**
```json
{
  "nombre": "string (3-100 caracteres)",
  "apellido": "string (3-100 caracteres)",
  "telefono": "string (regex: ^3\\d{9}$)"
}
```

**Response 200:**
```json
{
  "idCliente": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "documento": "12345678",
  "telefono": "3005551234"
}
```

**Errores:** 400, 404, 409, 500

---

#### `DELETE /api/v1/clientes/{id}`

Elimina cliente.

**Request:** ID en path  
**Response:** 204 No Content  
**Errores:** 404, 500

---

### 3️⃣ EmpleadoController (`/empleados`)

**Protegidos (requieren JWT)**

#### `GET /api/v1/empleados`

Lista todos los empleados.

**Response 200:**
```json
[
  {
    "idEmpleado": 1,
    "nombre": "María",
    "apellido": "García",
    "correo": "maria@salon.com",
    "telefono": "3001234567",
    "franjasLaborales": [
      {
        "idFranjaLaboral": 1,
        "dia": "MONDAY",
        "horaInicio": "08:00:00",
        "horaFin": "17:00:00"
      }
    ],
    "serviciosIds": [1, 2, 3]
  }
]
```

**Errores:** 500

---

#### `GET /api/v1/empleados/{id}`

Obtiene empleado por ID.

**Response 200:**
```json
{
  "idEmpleado": 1,
  "nombre": "María",
  "apellido": "García",
  "correo": "maria@salon.com",
  "telefono": "3001234567",
  "franjasLaborales": [...],
  "serviciosIds": [1, 2, 3]
}
```

**Errores:** 404, 500

---

#### `POST /api/v1/empleados`

Crea empleado con franjas laborales y calificaciones.

**Request:**
```json
{
  "nombre": "string",
  "apellido": "string",
  "correo": "string (email)",
  "telefono": "string (^3\\d{9}$)",
  "franjasLaborales": [
    {
      "dia": "enum MONDAY-SUNDAY",
      "horaInicio": "HH:mm:ss",
      "horaFin": "HH:mm:ss"
    }
  ],
  "serviciosIds": [1, 2, 3]
}
```

**Response 201:**
```json
{
  "idEmpleado": 1,
  "nombre": "María",
  "apellido": "García",
  "correo": "maria@salon.com",
  "telefono": "3001234567",
  "franjasLaborales": [...],
  "serviciosIds": [1, 2, 3]
}
```

**Errores:** 400, 404, 409, 500

---

#### `PUT /api/v1/empleados/{id}`

Actualiza empleado (campos opcionales).

**Request:**
```json
{
  "nombre": "string (2-100)",
  "apellido": "string (2-100)",
  "correo": "string (email)",
  "telefono": "string (^3\\d{9}$)"
}
```

**Response 200:** Empleado actualizado  
**Errores:** 400, 404, 409, 500

---

#### `DELETE /api/v1/empleados/{id}`

Elimina empleado (soft delete).

**Response:** 204 No Content  
**Errores:** 404, 500

---

### 4️⃣ ServicioController (`/servicios`)

#### `GET /api/v1/servicios`

Lista todos los servicios.

**Response 200:**
```json
[
  {
    "idServicio": 1,
    "nombre": "Corte de cabello",
    "duracion": 30,
    "recursos": [
      {
        "idRecurso": 1,
        "nombre": "Tijeras",
        "cantidadRequerida": 1
      }
    ]
  }
]
```

**Errores:** 500

---

#### `GET /api/v1/servicios/{id}`

Obtiene servicio por ID.

**Response 200:** Servicio  
**Errores:** 404, 500

---

#### `GET /api/v1/servicios/buscar?nombre=...`

Busca servicio por nombre exacto (case-insensitive).

**Query Parameters:**
- `nombre` (string, obligatorio)

**Response 200:** Servicio  
**Errores:** 404, 500

---

#### `POST /api/v1/servicios`

Crea servicio con recursos requeridos.

**Request:**
```json
{
  "nombre": "string (3-100, único)",
  "duracion": "int (mínimo 1)",
  "recursos": [
    {
      "idRecurso": 1,
      "cantidadRequerida": 2
    }
  ]
}
```

**Response 201:** Servicio creado  
**Errores:** 400, 409 (nombre duplicado), 500

---

#### `PUT /api/v1/servicios/{id}`

Actualiza servicio.

**Request:** Mismo contrato que POST  
**Response 200:** Servicio actualizado  
**Errores:** 400, 404, 409, 500

---

#### `DELETE /api/v1/servicios/{id}`

Elimina servicio.

**Response:** 204 No Content  
**Errores:** 404, 500

---

### 5️⃣ RecursoController (`/recursos`)

#### `GET /api/v1/recursos`

Lista todos los recursos.

**Response 200:**
```json
[
  {
    "idRecurso": 1,
    "nombre": "Tijeras",
    "cantidadTotal": 5
  }
]
```

**Errores:** 500

---

#### `GET /api/v1/recursos/{id}`

Obtiene recurso por ID.

**Response 200:** Recurso  
**Errores:** 404, 500

---

#### `GET /api/v1/recursos/buscar?nombre=...`

Busca recurso por nombre exacto (case-insensitive).

**Response 200:** Recurso  
**Errores:** 404, 500

---

#### `POST /api/v1/recursos`

Crea recurso.

**Request:**
```json
{
  "nombre": "string (3-100)",
  "cantidadTotal": "int (mínimo 1)"
}
```

**Response 201:** Recurso creado  
**Errores:** 400, 409, 500

---

#### `PUT /api/v1/recursos/{id}`

Actualiza recurso.

**Request:**
```json
{
  "nombre": "string (3-100)",
  "cantidadTotal": "int"
}
```

**Response 200:** Recurso actualizado  
**Errores:** 400, 404, 409, 500

---

#### `DELETE /api/v1/recursos/{id}`

Elimina recurso.

**Response:** 204 No Content  
**Errores:** 404, 500

---

### 6️⃣ CitaController (`/citas`)

#### `GET /api/v1/citas`

Lista todas las citas.

**Response 200:**
```json
[
  {
    "idCita": 1,
    "idCliente": 1,
    "idServicio": 1,
    "idEmpleado": 1,
    "fechaInicio": "2026-04-15T10:00:00",
    "fechaFin": "2026-04-15T10:30:00",
    "estado": "CONFIRMADA",
    "recursosAsignados": [
      {
        "idCitaRecurso": 1,
        "idRecurso": 1
      }
    ]
  }
]
```

**Errores:** 500

---

#### `GET /api/v1/citas/{idCita}`

Obtiene cita por ID.

**Response 200:** Cita  
**Errores:** 404, 500

---

#### `POST /api/v1/citas`

Crea cita con validaciones automáticas.

**Request:**
```json
{
  "idCliente": 1,
  "idServicio": 1,
  "idEmpleado": 1,
  "fechaInicio": "2026-04-15T10:00:00"
}
```

**Validaciones automáticas:**
- ✅ Empleado activo y calificado para servicio
- ✅ Empleado trabajando en ese horario (franja laboral)
- ✅ Sin solapamiento con otras citas
- ✅ Recursos disponibles en cantidad
- ✅ Fecha futura válida

**Response 201:** Cita creada (fechaFin calculada automáticamente)  
**Errores:** 400, 404, 409, 500

---

#### `PUT /api/v1/citas/{idCita}`

Actualiza estado de cita.

**Request:**
```json
{
  "estado": "enum PENDIENTE|CONFIRMADA|CANCELADA|REALIZADA"
}
```

**Response 200:** Cita actualizada  
**Errores:** 400, 404, 500

---

#### `DELETE /api/v1/citas/{idCita}`

Elimina/cancela cita.

**Response:** 204 No Content  
**Errores:** 404, 500

---

### 7️⃣ UsuarioController (`/usuarios`)

**Protegido: OWNER solamente**

#### `GET /api/v1/usuarios`

Lista todos los usuarios.

**Response 200:**
```json
[
  {
    "idUsuario": 1,
    "nombreUsuario": "admin",
    "rol": "OWNER",
    "idEmpleado": 1,
    "activo": true,
    "mfaEnabled": false
  }
]
```

**Errores:** 403, 500

---

#### `GET /api/v1/usuarios/{id}`

Obtiene usuario por ID.

**Response 200:** Usuario  
**Errores:** 403, 404, 500

---

#### `POST /api/v1/usuarios`

Crea usuario.

**Request:**
```json
{
  "nombreUsuario": "string (4-50, único)",
  "contrasena": "string (8-100)",
  "rol": "enum OWNER|RECEPTIONIST",
  "idEmpleado": 1
}
```

**Response 201:** Usuario creado  
**Errores:** 400, 403, 409, 500

---

#### `PUT /api/v1/usuarios/{id}`

Actualiza usuario (campos opcionales).

**Request:**
```json
{
  "nombreUsuario": "string (4-50)",
  "contrasena": "string (8-100)",
  "rol": "enum OWNER|RECEPTIONIST",
  "activo": "boolean"
}
```

**Response 200:** Usuario actualizado  
**Errores:** 400, 403, 404, 500

---

#### `DELETE /api/v1/usuarios/{id}`

Desactiva usuario (soft delete).

**Response:** 204 No Content  
**Errores:** 403, 404, 500

---

### 8️⃣ DisponibilidadController (`/disponibilidad`)

#### `GET /api/v1/disponibilidad`

Consulta horarios disponibles para agendar.

**Query Parameters:**
- `fecha` (YYYY-MM-DD, obligatorio, presente o futuro)
- `idServicio` (long, obligatorio, > 0)
- `idEmpleado` (long, opcional, > 0)

**Response 200:**
```json
{
  "fecha": "2026-04-15",
  "horariosDisponibles": [
    "08:00:00",
    "08:30:00",
    "09:00:00",
    "09:30:00"
  ]
}
```

**Nota:** Intervalos de 30 minutos. Considera:
- Franja laboral del empleado
- Citas existentes
- Recursos disponibles

**Errores:** 400, 404, 500

---

## DTOs Detallados

### Request DTOs

#### LoginRequest
```json
{
  "nombreUsuario": "string",
  "contrasena": "string"
}
```

#### MfaVerificationRequest
```json
{
  "nombreUsuario": "string",
  "totpCode": "string (6 dígitos)",
  "preAuthToken": "string"
}
```

#### MfaSetupRequest
```json
{
  "secret": "string (obligatorio, recibido en /setup)",
  "code": "string (6 dígitos, obligatorio)"
}
```

#### EmpleadoSignUpRequest
```json
{
  "email": "string (email)",
  "username": "string (4-20)",
  "password": "string (mínimo 8)"
}
```

#### CrearClienteRequest
```json
{
  "nombre": "string (3-100)",
  "apellido": "string (3-100)",
  "telefono": "string (^3\\d{9}$)",
  "documento": "string (^\\d{8,10}$)"
}
```

#### ActualizarClienteRequest
```json
{
  "nombre": "string (3-100)",
  "apellido": "string (3-100)",
  "telefono": "string (^3\\d{9}$)"
}
```

#### CrearFranjaRequest
```json
{
  "dia": "enum MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY",
  "horaInicio": "HH:mm:ss",
  "horaFin": "HH:mm:ss"
}
```

#### CrearEmpleadoRequest
```json
{
  "nombre": "string",
  "apellido": "string",
  "correo": "string (email)",
  "telefono": "string (^3\\d{9}$)",
  "franjasLaborales": [{ CrearFranjaRequest }],
  "serviciosIds": [1, 2, 3]
}
```

#### CrearServicioRequest
```json
{
  "nombre": "string (3-100)",
  "duracion": "int (mínimo 1)",
  "recursos": [
    {
      "idRecurso": 1,
      "cantidadRequerida": 2
    }
  ]
}
```

#### CrearRecursoRequest
```json
{
  "nombre": "string (3-100)",
  "cantidadTotal": "int (mínimo 1)"
}
```

#### CrearCitaRequest
```json
{
  "idCliente": 1,
  "idServicio": 1,
  "idEmpleado": 1,
  "fechaInicio": "2026-04-15T10:00:00"
}
```

#### ActualizarCitaRequest
```json
{
  "estado": "enum PENDIENTE|CONFIRMADA|CANCELADA|REALIZADA"
}
```

#### CrearUsuarioRequest
```json
{
  "nombreUsuario": "string (4-50)",
  "contrasena": "string (8-100)",
  "rol": "enum OWNER|RECEPTIONIST",
  "idEmpleado": 1
}
```

### Response DTOs

#### MfaSetupResponse
```json
{
  "secret": "string",
  "qrCodeUrl": "string (data:image/png;base64,...)"
}
```

#### AuthResponse
```json
{
  "token": "string"
}
```

#### EmpleadoSignUpResponse
```json
{
  "idEmpleado": 5,
  "nombreUsuario": "maria_lopez",
  "rol": "RECEPTIONIST"
}
```

---

## Ejemplos de Uso

### JavaScript/Fetch

```javascript
// Login
async function login() {
  const response = await fetch('http://localhost:8080/api/v1/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      nombreUsuario: 'admin',
      contrasena: 'password123'
    })
  });
  const data = await response.json();
  
  // Si MFA es requerido
  if (data.mfaRequired) {
    const preAuthToken = data.token;
    return verifyMfa(preAuthToken);
  }
  
  localStorage.setItem('token', data.token);
  return data;
}

// Verificar MFA
async function verifyMfa(preAuthToken) {
  const totpCode = prompt('Ingresa código TOTP de 6 dígitos:');
  const response = await fetch('http://localhost:8080/api/v1/auth/verify-mfa', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      nombreUsuario: 'admin',
      totpCode: totpCode,
      preAuthToken: preAuthToken
    })
  });
  const data = await response.json();
  localStorage.setItem('token', data.token);
  return data;
}

// Habilitar MFA
async function enableMfa() {
  const token = localStorage.getItem('token');
  
  // 1. Obtener QR code
  const qrResponse = await fetch('http://localhost:8080/api/v1/usuarios/me/mfa/setup', {
    method: 'GET',
    headers: { 'Authorization': `Bearer ${token}` }
  });
  const qrData = await qrResponse.json();
  console.log('Escanea este QR:', qrData.qrCodeUrl);
  
  // 2. Confirmar con código TOTP
  const code = prompt('Ingresa código TOTP después de escanear:');
  const confirmResponse = await fetch('http://localhost:8080/api/v1/usuarios/me/mfa/enable', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ secret: qrData.secret, code: code })
  });
  const confirmData = await confirmResponse.json();
  localStorage.setItem('token', confirmData.token);
  return confirmData;
}

// Registro público de empleado pre-registrado
async function employeeSignUp() {
  const response = await fetch('http://localhost:8080/api/v1/auth/employee-signup', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      email: 'maria.lopez@salon.com',
      username: 'maria_lopez',
      password: 'MiPassword123'
    })
  });
  return await response.json();
}

// Get clientes
async function getClientes() {
  const token = localStorage.getItem('token');
  const response = await fetch('http://localhost:8080/api/v1/clientes', {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  return await response.json();
}

// Crear cliente
async function createCliente(cliente) {
  const token = localStorage.getItem('token');
  const response = await fetch('http://localhost:8080/api/v1/clientes', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(cliente)
  });
  return await response.json();
}
```

### cURL

```bash
# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"nombreUsuario":"admin","contrasena":"password123"}'

# Verificar MFA (si mfaRequired: true)
curl -X POST http://localhost:8080/api/v1/auth/verify-mfa \
  -H "Content-Type: application/json" \
  -d '{
    "nombreUsuario": "admin",
    "totpCode": "123456",
    "preAuthToken": "<pre-auth-token>"
  }'

# Obtener QR para habilitar MFA
curl http://localhost:8080/api/v1/usuarios/me/mfa/setup \
  -H "Authorization: Bearer <token>"

# Habilitar MFA (confirmando con código TOTP)
curl -X POST http://localhost:8080/api/v1/usuarios/me/mfa/enable \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"secret":"<secret-from-setup>","code":"123456"}'

# Registro público de empleado pre-registrado
curl -X POST http://localhost:8080/api/v1/auth/employee-signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "maria.lopez@salon.com",
    "username": "maria_lopez",
    "password": "MiPassword123"
  }'

# Listar clientes
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/v1/clientes

# Crear cliente
curl -X POST http://localhost:8080/api/v1/clientes \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellido": "Pérez",
    "documento": "12345678",
    "telefono": "3005551234"
  }'

# Crear cita
curl -X POST http://localhost:8080/api/v1/citas \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "idCliente": 1,
    "idServicio": 1,
    "idEmpleado": 1,
    "fechaInicio": "2026-04-15T10:00:00"
  }'
```

---

## Manejo de Errores

### Excepciones Mapeadas

| Excepción | HTTP | Descripción |
|-----------|------|-------------|
| `ClienteNoEncontradoException` | 404 | Cliente no existe |
| `ClienteExistenteException` | 409 | Documento duplicado |
| `EmpleadoNoExistenteException` | 404 | Empleado no existe |
| `EmpleadoExistenteException` | 409 | Correo duplicado |
| `ServicioNoEncontradoException` | 404 | Servicio no existe |
| `ServicioExistenteException` | 409 | Nombre duplicado |
| `RecursoNoEncontradoException` | 404 | Recurso no existe |
| `RecursoExistenteException` | 409 | Recurso duplicado |
| `CitaNoEncontradaException` | 404 | Cita no existe |
| `CitaExistenteException` | 409 | Cita duplicada |
| `SolapamientoException` | 409 | Citas solapadas |
| `CitaFueraDeHorarioException` | 400 | Cita fuera de horario |
| `EmpleadoNoTrabajaEnEseHorarioException` | 400 | Empleado no trabaja esa hora |
| `UsuarioNoExistenteException` | 404 | Usuario no existe |
| `UsuarioExistenteException` | 409 | Usuario duplicado |
| `AccesoDenegadoException` | 403 | Sin permisos |
| `MethodArgumentNotValidException` | 400 | Validación fallida |
| `Exception` (genérica) | 500 | Error no esperado |

### Ejemplo de Respuesta de Error

```json
{
  "timestamp": "2026-04-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error en los datos enviados",
  "details": [
    "nombre: no debe estar vacío",
    "documento: formato inválido"
  ]
}
```

---

**Actualizado:** Abril 2026 | **Versión:** 0.0.1-SNAPSHOT


