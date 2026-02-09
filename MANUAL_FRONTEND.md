# 🎨 Manual de Integración Frontend - API Salón de Belleza

<div align="center">

![API REST](https://img.shields.io/badge/API-REST-blue?style=for-the-badge)
![JSON](https://img.shields.io/badge/Format-JSON-green?style=for-the-badge)
![JWT](https://img.shields.io/badge/Auth-JWT-orange?style=for-the-badge)

**Guía completa para desarrolladores frontend**

*Todos los endpoints · Ejemplos JSON · Códigos de respuesta · Manejo de errores*

</div>

---

## 📑 Tabla de Contenidos

1. [🚀 Inicio Rápido](#-inicio-rápido)
2. [🔐 Autenticación](#-autenticación)
3. [👤 Gestión de Clientes](#-gestión-de-clientes)
4. [👨‍💼 Gestión de Empleados](#-gestión-de-empleados)
5. [💇 Gestión de Servicios](#-gestión-de-servicios)
6. [🧴 Gestión de Recursos](#-gestión-de-recursos)
7. [📅 Gestión de Citas](#-gestión-de-citas)
8. [🔑 Gestión de Usuarios](#-gestión-de-usuarios)
9. [⚠️ Manejo de Errores](#️-manejo-de-errores)
10. [📋 Códigos de Estado HTTP](#-códigos-de-estado-http)
11. [💡 Mejores Prácticas](#-mejores-prácticas)

---

## 🚀 Inicio Rápido

### Configuración Base

```javascript
// Configuración de la API
const API_BASE_URL = 'http://localhost:8080/api/v1';

// Headers comunes
const getHeaders = (token = null) => ({
  'Content-Type': 'application/json',
  ...(token && { 'Authorization': `Bearer ${token}` })
});
```

### Estructura de Respuestas

Todas las respuestas exitosas siguen este patrón:

```json
{
  "campo1": "valor1",
  "campo2": "valor2"
}
```

Todas las respuestas de error siguen este patrón:

```json
{
  "timestamp": "2026-01-22T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Descripción del error",
  "path": "/api/v1/clientes"
}
```

---

## 🔐 Autenticación

### 1. Login (Sin MFA)

**Endpoint:** `POST /auth/login`  
**Autenticación requerida:** ❌ No

#### Request Body

```json
{
  "nombreUsuario": "recepcionista01",
  "contrasena": "password123"
}
```

#### Response (200 OK)

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZWNlcGNpb25pc3RhMDEiLCJyb2wiOiJSRUNFUFRJT05JU1QiLCJpYXQiOjE2NDAwMDAwMDAsImV4cCI6MTY0MDAwMzYwMH0.signature",
  "mfaRequired": false,
  "message": "Login exitoso"
}
```

#### Ejemplo JavaScript

```javascript
async function login(nombreUsuario, contrasena) {
  const response = await fetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify({ nombreUsuario, contrasena })
  });
  
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message);
  }
  
  const data = await response.json();
  
  if (!data.mfaRequired) {
    // Guardar token y redirigir
    localStorage.setItem('token', data.token);
    return { success: true, token: data.token };
  } else {
    // Requiere MFA
    return { success: false, requiresMfa: true, preAuthToken: data.token };
  }
}
```

### 2. Login (Con MFA - Paso 1)

**Endpoint:** `POST /auth/login`  
**Autenticación requerida:** ❌ No

#### Request Body

```json
{
  "nombreUsuario": "propietario",
  "contrasena": "securePass456"
}
```

#### Response (200 OK)

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJwcm9waWV0YXJpbyIsInR5cGUiOiJQUkVfQVVUSCIsImlhdCI6MTY0MDAwMDAwMCwiZXhwIjoxNjQwMDA5MDB9.signature",
  "mfaRequired": true,
  "message": "Se requiere verificación MFA. Use el código de su aplicación de autenticación."
}
```

### 3. Verificar MFA (Paso 2)

**Endpoint:** `POST /auth/verify-mfa`  
**Autenticación requerida:** ❌ No (usa token de pre-autenticación)

#### Request Body

```json
{
  "nombreUsuario": "propietario",
  "totpCode": "123456",
  "preAuthToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Response (200 OK)

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJwcm9waWV0YXJpbyIsInJvbCI6Ik9XTkVSIiwiaWF0IjoxNjQwMDAwMDAwLCJleHAiOjE2NDAwMDM2MDB9.signature",
  "mfaRequired": false,
  "message": "MFA verificado exitosamente"
}
```

#### Ejemplo JavaScript Completo (Con MFA)

```javascript
async function verifyMfa(nombreUsuario, totpCode, preAuthToken) {
  const response = await fetch(`${API_BASE_URL}/auth/verify-mfa`, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify({ nombreUsuario, totpCode, preAuthToken })
  });
  
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message);
  }
  
  const data = await response.json();
  localStorage.setItem('token', data.token);
  return data;
}

// Uso completo
async function handleLogin(nombreUsuario, contrasena) {
  try {
    const loginResult = await login(nombreUsuario, contrasena);
    
    if (loginResult.requiresMfa) {
      // Mostrar formulario para código MFA
      const totpCode = await promptForMfaCode();
      const mfaResult = await verifyMfa(
        nombreUsuario, 
        totpCode, 
        loginResult.preAuthToken
      );
      console.log('Login exitoso con MFA:', mfaResult);
    } else {
      console.log('Login exitoso:', loginResult);
    }
  } catch (error) {
    console.error('Error de autenticación:', error.message);
  }
}
```

### Errores Comunes de Autenticación

| Código | Descripción | Solución |
|--------|-------------|----------|
| 401 | Credenciales inválidas | Verificar usuario y contraseña |
| 401 | Código MFA incorrecto | Generar nuevo código (expira cada 30s) |
| 401 | Token expirado | Hacer login nuevamente |
| 400 | Datos faltantes | Verificar que todos los campos estén presentes |

---

## 👤 Gestión de Clientes

Base URL: `/clientes`

### 1. Listar Todos los Clientes

**Endpoint:** `GET /clientes`  
**Autenticación:** ✅ Requerida

#### Response (200 OK)

```json
[
  {
    "idCliente": 1,
    "nombre": "Juan",
    "apellido": "Perez",
    "documento": "1234567890",
    "telefono": "3001234567"
  },
  {
    "idCliente": 2,
    "nombre": "María",
    "apellido": "González",
    "documento": "9876543210",
    "telefono": "3009876543"
  }
]
```

#### Ejemplo JavaScript

```javascript
async function listarClientes() {
  const token = localStorage.getItem('token');
  const response = await fetch(`${API_BASE_URL}/clientes`, {
    method: 'GET',
    headers: getHeaders(token)
  });
  
  if (!response.ok) throw new Error('Error al listar clientes');
  return await response.json();
}
```

### 2. Buscar Cliente por ID

**Endpoint:** `GET /clientes/{id}`  
**Autenticación:** ✅ Requerida

#### Response (200 OK)

```json
{
  "idCliente": 1,
  "nombre": "Juan",
  "apellido": "Perez",
  "documento": "1234567890",
  "telefono": "3001234567"
}
```

#### Ejemplo JavaScript

```javascript
async function buscarClientePorId(id) {
  const token = localStorage.getItem('token');
  const response = await fetch(`${API_BASE_URL}/clientes/${id}`, {
    method: 'GET',
    headers: getHeaders(token)
  });
  
  if (response.status === 404) {
    throw new Error('Cliente no encontrado');
  }
  if (!response.ok) throw new Error('Error al buscar cliente');
  
  return await response.json();
}
```

### 3. Búsqueda Avanzada de Clientes

**Endpoint:** `GET /clientes/buscar?telefono=XXX&documento=YYY&nombre=ZZZ`  
**Autenticación:** ✅ Requerida

#### Búsqueda por Teléfono (Único resultado)

**Request:** `GET /clientes/buscar?telefono=3001234567`

**Response (200 OK):**
```json
{
  "idCliente": 1,
  "nombre": "Juan",
  "apellido": "Perez",
  "documento": "1234567890",
  "telefono": "3001234567"
}
```

#### Búsqueda por Nombre (Múltiples resultados)

**Request:** `GET /clientes/buscar?nombre=Juan`

**Response (200 OK):**
```json
[
  {
    "idCliente": 1,
    "nombre": "Juan",
    "apellido": "Perez",
    "documento": "1234567890",
    "telefono": "3001234567"
  },
  {
    "idCliente": 5,
    "nombre": "Juan Carlos",
    "apellido": "Rodríguez",
    "documento": "5555555555",
    "telefono": "3005555555"
  }
]
```

#### Ejemplo JavaScript

```javascript
async function buscarClientes({ telefono, documento, nombre }) {
  const token = localStorage.getItem('token');
  const params = new URLSearchParams();
  
  if (telefono) params.append('telefono', telefono);
  if (documento) params.append('documento', documento);
  if (nombre) params.append('nombre', nombre);
  
  const response = await fetch(
    `${API_BASE_URL}/clientes/buscar?${params}`, 
    {
      method: 'GET',
      headers: getHeaders(token)
    }
  );
  
  if (response.status === 404) {
    return null; // No se encontraron resultados
  }
  if (!response.ok) throw new Error('Error en la búsqueda');
  
  return await response.json();
}

// Uso
const clientePorTelefono = await buscarClientes({ telefono: '3001234567' });
const clientesPorNombre = await buscarClientes({ nombre: 'Juan' });
```

### 4. Crear Nuevo Cliente

**Endpoint:** `POST /clientes`  
**Autenticación:** ✅ Requerida

#### Request Body

```json
{
  "nombre": "Pedro",
  "apellido": "Martínez",
  "documento": "1122334455",
  "telefono": "3112233445"
}
```

#### Validaciones

- `nombre`: 3-100 caracteres, obligatorio
- `apellido`: 3-100 caracteres, obligatorio
- `telefono`: 10 dígitos, debe empezar con 3 (formato colombiano)
- `documento`: 8-10 dígitos numéricos, obligatorio

#### Response (201 Created)

```json
{
  "idCliente": 10,
  "nombre": "Pedro",
  "apellido": "Martínez",
  "documento": "1122334455",
  "telefono": "3112233445"
}
```

#### Ejemplo JavaScript

```javascript
async function crearCliente(clienteData) {
  const token = localStorage.getItem('token');
  const response = await fetch(`${API_BASE_URL}/clientes`, {
    method: 'POST',
    headers: getHeaders(token),
    body: JSON.stringify(clienteData)
  });
  
  if (response.status === 409) {
    const error = await response.json();
    throw new Error('Cliente ya existe: ' + error.message);
  }
  if (response.status === 400) {
    const error = await response.json();
    throw new Error('Datos inválidos: ' + error.message);
  }
  if (!response.ok) throw new Error('Error al crear cliente');
  
  return await response.json();
}

// Uso con validación
try {
  const nuevoCliente = await crearCliente({
    nombre: 'Pedro',
    apellido: 'Martínez',
    documento: '1122334455',
    telefono: '3112233445'
  });
  console.log('Cliente creado:', nuevoCliente);
} catch (error) {
  console.error(error.message);
}
```

### 5. Actualizar Cliente

**Endpoint:** `PUT /clientes/{id}`  
**Autenticación:** ✅ Requerida

#### Request Body (Actualización parcial)

```json
{
  "telefono": "3119988776"
}
```

O actualización completa:

```json
{
  "nombre": "Pedro",
  "apellido": "Martínez López",
  "documento": "1122334455",
  "telefono": "3119988776"
}
```

#### Response (200 OK)

```json
{
  "idCliente": 10,
  "nombre": "Pedro",
  "apellido": "Martínez López",
  "documento": "1122334455",
  "telefono": "3119988776"
}
```

#### Ejemplo JavaScript

```javascript
async function actualizarCliente(id, datosActualizados) {
  const token = localStorage.getItem('token');
  const response = await fetch(`${API_BASE_URL}/clientes/${id}`, {
    method: 'PUT',
    headers: getHeaders(token),
    body: JSON.stringify(datosActualizados)
  });
  
  if (response.status === 404) {
    throw new Error('Cliente no encontrado');
  }
  if (!response.ok) throw new Error('Error al actualizar cliente');
  
  return await response.json();
}
```

### 6. Eliminar Cliente

**Endpoint:** `DELETE /clientes/{id}`  
**Autenticación:** ✅ Requerida

#### Response (204 No Content)

No retorna contenido en el body.

#### Ejemplo JavaScript

```javascript
async function eliminarCliente(id) {
  const token = localStorage.getItem('token');
  const response = await fetch(`${API_BASE_URL}/clientes/${id}`, {
    method: 'DELETE',
    headers: getHeaders(token)
  });
  
  if (response.status === 404) {
    throw new Error('Cliente no encontrado');
  }
  if (!response.ok) throw new Error('Error al eliminar cliente');
  
  return true; // Eliminación exitosa
}
```

---

## 👨‍💼 Gestión de Empleados

Base URL: `/empleados`

### 1. Listar Todos los Empleados

**Endpoint:** `GET /empleados`  
**Autenticación:** ✅ Requerida

#### Response (200 OK)

```json
[
  {
    "idEmpleado": 1,
    "nombre": "Ana",
    "apellido": "López",
    "correo": "ana.lopez@salon.com",
    "telefono": "3201234567",
    "franjasLaborales": [
      {
        "idFranjaLaboral": 1,
        "dia": "MONDAY",
        "horaInicio": "09:00:00",
        "horaFin": "17:00:00"
      },
      {
        "idFranjaLaboral": 2,
        "dia": "TUESDAY",
        "horaInicio": "09:00:00",
        "horaFin": "17:00:00"
      }
    ],
    "calificaciones": [
      {
        "idCalificacion": 1,
        "idServicio": 1,
        "nombreServicio": "Corte de Cabello"
      },
      {
        "idCalificacion": 2,
        "idServicio": 3,
        "nombreServicio": "Tinte"
      }
    ]
  }
]
```

### 2. Crear Empleado

**Endpoint:** `POST /empleados`  
**Autenticación:** ✅ Requerida

#### Request Body

```json
{
  "nombre": "Carlos",
  "apellido": "Ramírez",
  "correo": "carlos.ramirez@salon.com",
  "telefono": "3157894561",
  "franjasLaborales": [
    {
      "dia": "MONDAY",
      "horaInicio": "08:00",
      "horaFin": "16:00"
    },
    {
      "dia": "WEDNESDAY",
      "horaInicio": "08:00",
      "horaFin": "16:00"
    },
    {
      "dia": "FRIDAY",
      "horaInicio": "08:00",
      "horaFin": "16:00"
    }
  ],
  "idsServicios": [1, 3, 5]
}
```

#### Valores válidos para `dia`

- `MONDAY` - Lunes
- `TUESDAY` - Martes
- `WEDNESDAY` - Miércoles
- `THURSDAY` - Jueves
- `FRIDAY` - Viernes
- `SATURDAY` - Sábado
- `SUNDAY` - Domingo

#### Formato de Hora

- `horaInicio` y `horaFin`: Formato "HH:mm" (24 horas)
- Ejemplos: "08:00", "14:30", "17:45"

#### Response (201 Created)

```json
{
  "idEmpleado": 5,
  "nombre": "Carlos",
  "apellido": "Ramírez",
  "correo": "carlos.ramirez@salon.com",
  "telefono": "3157894561",
  "franjasLaborales": [
    {
      "idFranjaLaboral": 10,
      "dia": "MONDAY",
      "horaInicio": "08:00:00",
      "horaFin": "16:00:00"
    },
    {
      "idFranjaLaboral": 11,
      "dia": "WEDNESDAY",
      "horaInicio": "08:00:00",
      "horaFin": "16:00:00"
    },
    {
      "idFranjaLaboral": 12,
      "dia": "FRIDAY",
      "horaInicio": "08:00:00",
      "horaFin": "16:00:00"
    }
  ],
  "calificaciones": [
    {
      "idCalificacion": 15,
      "idServicio": 1,
      "nombreServicio": "Corte de Cabello"
    },
    {
      "idCalificacion": 16,
      "idServicio": 3,
      "nombreServicio": "Tinte"
    },
    {
      "idCalificacion": 17,
      "idServicio": 5,
      "nombreServicio": "Manicure"
    }
  ]
}
```

#### Ejemplo JavaScript

```javascript
async function crearEmpleado(empleadoData) {
  const token = localStorage.getItem('token');
  const response = await fetch(`${API_BASE_URL}/empleados`, {
    method: 'POST',
    headers: getHeaders(token),
    body: JSON.stringify(empleadoData)
  });
  
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message);
  }
  
  return await response.json();
}

// Ejemplo de uso
const nuevoEmpleado = await crearEmpleado({
  nombre: 'Carlos',
  apellido: 'Ramírez',
  correo: 'carlos.ramirez@salon.com',
  telefono: '3157894561',
  franjasLaborales: [
    { dia: 'MONDAY', horaInicio: '08:00', horaFin: '16:00' },
    { dia: 'WEDNESDAY', horaInicio: '08:00', horaFin: '16:00' }
  ],
  idsServicios: [1, 3, 5]
});
```

### 3. Actualizar Empleado

**Endpoint:** `PUT /empleados/{id}`  
**Autenticación:** ✅ Requerida

#### Request Body (Actualización parcial o completa)

```json
{
  "telefono": "3159999999",
  "franjasLaborales": [
    {
      "dia": "MONDAY",
      "horaInicio": "09:00",
      "horaFin": "18:00"
    }
  ]
}
```

---

## 💇 Gestión de Servicios

Base URL: `/servicios`

### 1. Listar Todos los Servicios

**Endpoint:** `GET /servicios`  
**Autenticación:** ✅ Requerida

#### Response (200 OK)

```json
[
  {
    "idServicio": 1,
    "nombre": "Corte de Cabello",
    "duracion": 30,
    "recursosRequeridos": [
      {
        "idRecurso": 1,
        "nombreRecurso": "Tijeras",
        "cantidadRequerida": 1
      },
      {
        "idRecurso": 5,
        "nombreRecurso": "Capa",
        "cantidadRequerida": 1
      }
    ]
  },
  {
    "idServicio": 2,
    "nombre": "Tinte Completo",
    "duracion": 120,
    "recursosRequeridos": [
      {
        "idRecurso": 3,
        "nombreRecurso": "Tinte",
        "cantidadRequerida": 2
      },
      {
        "idRecurso": 4,
        "nombreRecurso": "Guantes",
        "cantidadRequerida": 1
      }
    ]
  }
]
```

### 2. Crear Servicio

**Endpoint:** `POST /servicios`  
**Autenticación:** ✅ Requerida

#### Request Body

```json
{
  "nombre": "Pedicure Spa",
  "duracion": 60,
  "recursosRequeridos": [
    {
      "idRecurso": 10,
      "cantidadRequerida": 1
    },
    {
      "idRecurso": 12,
      "cantidadRequerida": 2
    }
  ]
}
```

#### Validaciones

- `nombre`: Único, 3-100 caracteres
- `duracion`: En minutos, mayor a 0
- `recursosRequeridos`: Array con objetos { idRecurso, cantidadRequerida }

#### Response (201 Created)

```json
{
  "idServicio": 8,
  "nombre": "Pedicure Spa",
  "duracion": 60,
  "recursosRequeridos": [
    {
      "idRecurso": 10,
      "nombreRecurso": "Kit de Pedicure",
      "cantidadRequerida": 1
    },
    {
      "idRecurso": 12,
      "nombreRecurso": "Toallas",
      "cantidadRequerida": 2
    }
  ]
}
```

---

## 🧴 Gestión de Recursos

Base URL: `/recursos`

### 1. Listar Todos los Recursos

**Endpoint:** `GET /recursos`  
**Autenticación:** ✅ Requerida

#### Response (200 OK)

```json
[
  {
    "idRecurso": 1,
    "nombre": "Tijeras Profesionales",
    "cantidadTotal": 15
  },
  {
    "idRecurso": 2,
    "nombre": "Secador de Pelo",
    "cantidadTotal": 8
  },
  {
    "idRecurso": 3,
    "nombre": "Tinte Color Negro",
    "cantidadTotal": 20
  }
]
```

### 2. Crear Recurso

**Endpoint:** `POST /recursos`  
**Autenticación:** ✅ Requerida

#### Request Body

```json
{
  "nombre": "Plancha de Cabello",
  "cantidadTotal": 5
}
```

#### Response (201 Created)

```json
{
  "idRecurso": 15,
  "nombre": "Plancha de Cabello",
  "cantidadTotal": 5
}
```

### 3. Actualizar Recurso

**Endpoint:** `PUT /recursos/{id}`  
**Autenticación:** ✅ Requerida

#### Request Body

```json
{
  "cantidadTotal": 10
}
```

---

## 📅 Gestión de Citas

Base URL: `/citas`

### 1. Listar Todas las Citas

**Endpoint:** `GET /citas`  
**Autenticación:** ✅ Requerida

#### Response (200 OK)

```json
[
  {
    "idCita": 1,
    "cliente": {
      "idCliente": 1,
      "nombre": "Juan Perez"
    },
    "empleado": {
      "idEmpleado": 2,
      "nombre": "Ana López"
    },
    "servicio": {
      "idServicio": 1,
      "nombre": "Corte de Cabello",
      "duracion": 30
    },
    "fechaInicio": "2026-01-25T10:00:00",
    "fechaFin": "2026-01-25T10:30:00",
    "estado": "PENDIENTE",
    "fechaCreacion": "2026-01-20T15:30:00",
    "recursosAsignados": [
      {
        "idRecurso": 1,
        "nombreRecurso": "Tijeras",
        "cantidadReservada": 1
      }
    ]
  }
]
```

### 2. Crear Cita

**Endpoint:** `POST /citas`  
**Autenticación:** ✅ Requerida

#### Request Body

```json
{
  "idCliente": 1,
  "idServicio": 1,
  "idEmpleado": 2,
  "fechaInicio": "2026-01-25T10:00:00"
}
```

#### Validaciones Automáticas

El sistema valida automáticamente:

1. ✅ Cliente existe
2. ✅ Servicio existe
3. ✅ Empleado existe
4. ✅ Empleado está calificado para ese servicio
5. ✅ Fecha es futura
6. ✅ Empleado está disponible (horario laboral)
7. ✅ No hay solapamiento con otras citas
8. ✅ Recursos disponibles

#### Response (201 Created)

```json
{
  "idCita": 15,
  "cliente": {
    "idCliente": 1,
    "nombre": "Juan Perez"
  },
  "empleado": {
    "idEmpleado": 2,
    "nombre": "Ana López"
  },
  "servicio": {
    "idServicio": 1,
    "nombre": "Corte de Cabello",
    "duracion": 30
  },
  "fechaInicio": "2026-01-25T10:00:00",
  "fechaFin": "2026-01-25T10:30:00",
  "estado": "PENDIENTE",
  "fechaCreacion": "2026-01-22T16:45:00",
  "recursosAsignados": [
    {
      "idRecurso": 1,
      "nombreRecurso": "Tijeras",
      "cantidadReservada": 1
    },
    {
      "idRecurso": 5,
      "nombreRecurso": "Capa",
      "cantidadReservada": 1
    }
  ]
}
```

#### Ejemplo JavaScript con Manejo de Errores

```javascript
async function crearCita(citaData) {
  const token = localStorage.getItem('token');
  
  try {
    const response = await fetch(`${API_BASE_URL}/citas`, {
      method: 'POST',
      headers: getHeaders(token),
      body: JSON.stringify(citaData)
    });
    
    if (!response.ok) {
      const error = await response.json();
      
      // Manejar errores específicos
      if (response.status === 409) {
        if (error.message.includes('solapamiento')) {
          throw new Error('El empleado ya tiene una cita en ese horario');
        }
        if (error.message.includes('recursos')) {
          throw new Error('No hay recursos disponibles para esa fecha');
        }
      }
      
      if (response.status === 400) {
        throw new Error('Datos inválidos: ' + error.message);
      }
      
      throw new Error(error.message);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error al crear cita:', error);
    throw error;
  }
}

// Uso con feedback al usuario
try {
  const nuevaCita = await crearCita({
    idCliente: 1,
    idServicio: 1,
    idEmpleado: 2,
    fechaInicio: '2026-01-25T10:00:00'
  });
  
  alert('Cita creada exitosamente');
  console.log(nuevaCita);
} catch (error) {
  alert('Error: ' + error.message);
}
```

### 3. Actualizar Estado de Cita

**Endpoint:** `PUT /citas/{id}`  
**Autenticación:** ✅ Requerida

#### Request Body

```json
{
  "estado": "CONFIRMADA"
}
```

#### Estados Válidos

- `PENDIENTE` - Cita creada, pendiente de confirmación
- `CONFIRMADA` - Cliente confirmó la cita
- `CANCELADA` - Cita cancelada
- `REALIZADA` - Servicio completado

#### Response (200 OK)

```json
{
  "idCita": 15,
  "cliente": { "idCliente": 1, "nombre": "Juan Perez" },
  "empleado": { "idEmpleado": 2, "nombre": "Ana López" },
  "servicio": { "idServicio": 1, "nombre": "Corte de Cabello", "duracion": 30 },
  "fechaInicio": "2026-01-25T10:00:00",
  "fechaFin": "2026-01-25T10:30:00",
  "estado": "CONFIRMADA",
  "fechaCreacion": "2026-01-22T16:45:00",
  "recursosAsignados": [...]
}
```

---

## 🔑 Gestión de Usuarios

Base URL: `/usuarios`

### 1. Listar Todos los Usuarios

**Endpoint:** `GET /usuarios`  
**Autenticación:** ✅ Requerida

#### Response (200 OK)

```json
[
  {
    "idUsuario": 1,
    "nombreUsuario": "propietario",
    "rol": "OWNER",
    "idEmpleado": 1,
    "mfaEnable": true,
    "activo": true
  },
  {
    "idUsuario": 2,
    "nombreUsuario": "recepcionista01",
    "rol": "RECEPTIONIST",
    "idEmpleado": 3,
    "mfaEnable": false,
    "activo": true
  }
]
```

**Nota:** La contraseña nunca se retorna en las respuestas.

### 2. Crear Usuario

**Endpoint:** `POST /usuarios`  
**Autenticación:** ✅ Requerida

#### Request Body

```json
{
  "nombreUsuario": "recepcionista02",
  "contrasena": "Password123!",
  "rol": "RECEPTIONIST",
  "idEmpleado": 5,
  "mfaEnable": false
}
```

#### Roles Válidos

- `OWNER` - Propietario (requiere MFA obligatorio)
- `RECEPTIONIST` - Recepcionista (MFA opcional)

#### Validaciones

- `nombreUsuario`: Único en el sistema
- `idEmpleado`: Debe existir y no tener usuario asignado (relación 1:1)
- `contrasena`: Se encripta automáticamente con BCrypt

#### Response (201 Created)

```json
{
  "idUsuario": 8,
  "nombreUsuario": "recepcionista02",
  "rol": "RECEPTIONIST",
  "idEmpleado": 5,
  "mfaEnable": false,
  "activo": true
}
```

### 3. Actualizar Usuario

**Endpoint:** `PUT /usuarios/{id}`  
**Autenticación:** ✅ Requerida

#### Request Body (Cambiar contraseña)

```json
{
  "contrasena": "NuevaPassword456!"
}
```

O actualizar otros campos:

```json
{
  "mfaEnable": true,
  "activo": true
}
```

### 4. Desactivar Usuario (Soft Delete)

**Endpoint:** `DELETE /usuarios/{id}`  
**Autenticación:** ✅ Requerida

#### Response (204 No Content)

No retorna contenido. El usuario se marca como `activo: false` pero no se elimina de la base de datos.

---

## ⚠️ Manejo de Errores

### Estructura de Error Estándar

```json
{
  "timestamp": "2026-01-22T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "El nombre del cliente es obligatorio",
  "path": "/api/v1/clientes"
}
```

### Tipos de Errores Comunes

#### 1. Errores de Validación (400 Bad Request)

```json
{
  "timestamp": "2026-01-22T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "El teléfono debe tener 10 dígitos y empezar por 3",
  "path": "/api/v1/clientes"
}
```

#### 2. Recurso No Encontrado (404 Not Found)

```json
{
  "timestamp": "2026-01-22T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "No se encontró el cliente con ID: 999",
  "path": "/api/v1/clientes/999"
}
```

#### 3. Conflicto de Datos (409 Conflict)

```json
{
  "timestamp": "2026-01-22T10:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Ya existe un cliente con el teléfono: 3001234567",
  "path": "/api/v1/clientes"
}
```

#### 4. No Autorizado (401 Unauthorized)

```json
{
  "timestamp": "2026-01-22T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token JWT inválido o expirado",
  "path": "/api/v1/clientes"
}
```

### Función Helper para Manejo de Errores

```javascript
function handleApiError(error, response) {
  const errorMap = {
    400: 'Datos inválidos o incompletos',
    401: 'No autorizado. Por favor inicie sesión nuevamente',
    404: 'Recurso no encontrado',
    409: 'El recurso ya existe o hay un conflicto',
    500: 'Error del servidor. Intente nuevamente más tarde'
  };
  
  const defaultMessage = errorMap[response.status] || 'Error desconocido';
  const userMessage = error.message || defaultMessage;
  
  return {
    status: response.status,
    message: userMessage,
    timestamp: error.timestamp
  };
}

// Uso
try {
  const response = await fetch(url, options);
  if (!response.ok) {
    const error = await response.json();
    const handledError = handleApiError(error, response);
    throw new Error(handledError.message);
  }
} catch (err) {
  console.error(err.message);
  alert(err.message);
}
```

---

## 📋 Códigos de Estado HTTP

| Código | Significado | Cuándo se usa |
|--------|-------------|---------------|
| 200 | OK | GET, PUT exitosos |
| 201 | Created | POST exitoso (recurso creado) |
| 204 | No Content | DELETE exitoso |
| 400 | Bad Request | Datos inválidos, validaciones fallidas |
| 401 | Unauthorized | Token inválido, expirado o faltante |
| 404 | Not Found | Recurso no existe |
| 409 | Conflict | Recurso duplicado, conflicto de negocio |
| 500 | Internal Server Error | Error del servidor |

---

## 💡 Mejores Prácticas

### 1. Manejo de Token JWT

```javascript
// Guardar token después del login
localStorage.setItem('token', data.token);

// Leer token antes de cada request
const token = localStorage.getItem('token');

// Limpiar token al cerrar sesión
function logout() {
  localStorage.removeItem('token');
  window.location.href = '/login';
}

// Verificar si el token está presente
function isAuthenticated() {
  return !!localStorage.getItem('token');
}

// Interceptar errores 401 y redirigir al login
if (response.status === 401) {
  logout();
}
```

### 2. Función Genérica para Requests

```javascript
async function apiRequest(endpoint, method = 'GET', body = null) {
  const token = localStorage.getItem('token');
  const config = {
    method,
    headers: getHeaders(token)
  };
  
  if (body) {
    config.body = JSON.stringify(body);
  }
  
  try {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
    
    // Manejar token expirado
    if (response.status === 401) {
      logout();
      throw new Error('Sesión expirada');
    }
    
    // Si es 204 (No Content), no hay body
    if (response.status === 204) {
      return { success: true };
    }
    
    const data = await response.json();
    
    if (!response.ok) {
      throw new Error(data.message || 'Error en la petición');
    }
    
    return data;
  } catch (error) {
    console.error(`Error en ${method} ${endpoint}:`, error);
    throw error;
  }
}

// Uso
const clientes = await apiRequest('/clientes', 'GET');
const nuevoCliente = await apiRequest('/clientes', 'POST', clienteData);
```

### 3. Validación en el Frontend

```javascript
// Validar teléfono colombiano
function validarTelefono(telefono) {
  const regex = /^3\d{9}$/;
  return regex.test(telefono);
}

// Validar documento
function validarDocumento(documento) {
  const regex = /^\d{8,10}$/;
  return regex.test(documento);
}

// Validar antes de enviar
function validarClienteForm(data) {
  const errores = [];
  
  if (!data.nombre || data.nombre.length < 3) {
    errores.push('El nombre debe tener al menos 3 caracteres');
  }
  
  if (!validarTelefono(data.telefono)) {
    errores.push('Teléfono inválido (debe ser 10 dígitos iniciando con 3)');
  }
  
  if (!validarDocumento(data.documento)) {
    errores.push('Documento inválido (debe tener 8-10 dígitos)');
  }
  
  return errores;
}
```

### 4. Formateo de Fechas

```javascript
// Convertir fecha del input a formato ISO
function formatearFechaParaAPI(fecha, hora) {
  // fecha: "2026-01-25", hora: "10:00"
  return `${fecha}T${hora}:00`;
}

// Formatear fecha de respuesta para mostrar
function formatearFechaParaMostrar(fechaISO) {
  const fecha = new Date(fechaISO);
  return fecha.toLocaleString('es-CO', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
}

// Uso
const fechaAPI = formatearFechaParaAPI('2026-01-25', '10:00');
// "2026-01-25T10:00:00"

const fechaLegible = formatearFechaParaMostrar('2026-01-25T10:00:00');
// "25 de enero de 2026, 10:00 a. m."
```

### 5. Manejo de Loading States

```javascript
// Estado de carga global
let isLoading = false;

function setLoading(loading) {
  isLoading = loading;
  // Mostrar/ocultar spinner
  document.getElementById('spinner').style.display = loading ? 'block' : 'none';
}

async function fetchConLoading(promesa) {
  setLoading(true);
  try {
    const resultado = await promesa;
    return resultado;
  } finally {
    setLoading(false);
  }
}

// Uso
const clientes = await fetchConLoading(
  apiRequest('/clientes', 'GET')
);
```

### 6. Caché Local (Opcional)

```javascript
// Cache simple con expiración
const cache = {
  set(key, data, expirationMinutes = 5) {
    const item = {
      data,
      expiration: Date.now() + (expirationMinutes * 60 * 1000)
    };
    localStorage.setItem(`cache_${key}`, JSON.stringify(item));
  },
  
  get(key) {
    const itemStr = localStorage.getItem(`cache_${key}`);
    if (!itemStr) return null;
    
    const item = JSON.parse(itemStr);
    if (Date.now() > item.expiration) {
      localStorage.removeItem(`cache_${key}`);
      return null;
    }
    
    return item.data;
  },
  
  clear(key) {
    if (key) {
      localStorage.removeItem(`cache_${key}`);
    } else {
      // Limpiar todo el cache
      Object.keys(localStorage)
        .filter(k => k.startsWith('cache_'))
        .forEach(k => localStorage.removeItem(k));
    }
  }
};

// Uso
async function getClientesConCache() {
  const cached = cache.get('clientes');
  if (cached) return cached;
  
  const clientes = await apiRequest('/clientes', 'GET');
  cache.set('clientes', clientes, 5); // 5 minutos
  return clientes;
}
```

---

## 🎓 Ejemplo Completo: Formulario de Citas

```javascript
// HTML
/*
<form id="formCita">
  <select id="cliente" required></select>
  <select id="servicio" required></select>
  <select id="empleado" required></select>
  <input type="date" id="fecha" required>
  <input type="time" id="hora" required>
  <button type="submit">Crear Cita</button>
</form>
*/

// JavaScript
document.addEventListener('DOMContentLoaded', async () => {
  // Cargar datos iniciales
  await cargarClientes();
  await cargarServicios();
  
  // Event listeners
  document.getElementById('servicio').addEventListener('change', cargarEmpleados);
  document.getElementById('formCita').addEventListener('submit', crearCita);
});

async function cargarClientes() {
  try {
    const clientes = await apiRequest('/clientes', 'GET');
    const select = document.getElementById('cliente');
    
    clientes.forEach(cliente => {
      const option = document.createElement('option');
      option.value = cliente.idCliente;
      option.textContent = `${cliente.nombre} ${cliente.apellido}`;
      select.appendChild(option);
    });
  } catch (error) {
    alert('Error al cargar clientes: ' + error.message);
  }
}

async function cargarServicios() {
  try {
    const servicios = await apiRequest('/servicios', 'GET');
    const select = document.getElementById('servicio');
    
    servicios.forEach(servicio => {
      const option = document.createElement('option');
      option.value = servicio.idServicio;
      option.textContent = `${servicio.nombre} (${servicio.duracion} min)`;
      select.appendChild(option);
    });
  } catch (error) {
    alert('Error al cargar servicios: ' + error.message);
  }
}

async function cargarEmpleados() {
  try {
    const idServicio = document.getElementById('servicio').value;
    const empleados = await apiRequest('/empleados', 'GET');
    
    // Filtrar empleados calificados para el servicio
    const empleadosCalificados = empleados.filter(emp =>
      emp.calificaciones.some(cal => cal.idServicio == idServicio)
    );
    
    const select = document.getElementById('empleado');
    select.innerHTML = '<option value="">Seleccione un empleado</option>';
    
    empleadosCalificados.forEach(emp => {
      const option = document.createElement('option');
      option.value = emp.idEmpleado;
      option.textContent = `${emp.nombre} ${emp.apellido}`;
      select.appendChild(option);
    });
  } catch (error) {
    alert('Error al cargar empleados: ' + error.message);
  }
}

async function crearCita(event) {
  event.preventDefault();
  
  const citaData = {
    idCliente: parseInt(document.getElementById('cliente').value),
    idServicio: parseInt(document.getElementById('servicio').value),
    idEmpleado: parseInt(document.getElementById('empleado').value),
    fechaInicio: formatearFechaParaAPI(
      document.getElementById('fecha').value,
      document.getElementById('hora').value
    )
  };
  
  try {
    setLoading(true);
    const nuevaCita = await apiRequest('/citas', 'POST', citaData);
    alert('¡Cita creada exitosamente!');
    console.log(nuevaCita);
    
    // Limpiar formulario
    event.target.reset();
  } catch (error) {
    alert('Error al crear cita: ' + error.message);
  } finally {
    setLoading(false);
  }
}
```

---

## 📞 Soporte

Para más información, consulta:
- **Documentación completa**: [DOCUMENTACION.md](./DOCUMENTACION.md)
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

---

<div align="center">

**🎨 Manual de Integración Frontend v1.0**

*Última actualización: Enero 22, 2026*

</div>
