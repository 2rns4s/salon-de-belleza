# Manual Frontend - API Salón de Belleza

![API REST](https://img.shields.io/badge/API-REST-blue) ![JSON](https://img.shields.io/badge/Format-JSON-green) ![JWT](https://img.shields.io/badge/Auth-JWT-orange)

Guía práctica de integración frontend con ejemplos JavaScript.

---

## Configuración Base

```javascript
const API_BASE_URL = 'http://localhost:8080/api/v1';

const getHeaders = (token = null) => ({
  'Content-Type': 'application/json',
  ...(token && { 'Authorization': `Bearer ${token}` })
});

// Función genérica para requests
async function apiRequest(endpoint, method = 'GET', body = null) {
  const token = localStorage.getItem('token');
  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    method,
    headers: getHeaders(token),
    body: body ? JSON.stringify(body) : null
  });

  if (response.status === 401) {
    localStorage.removeItem('token');
    throw new Error('Sesión expirada');
  }
  if (response.status === 204) return { success: true };

  const data = await response.json();
  if (!response.ok) throw new Error(data.message || 'Error');
  return data;
}
```

---

## Autenticación

### Login

```javascript
// Sin MFA
const { token } = await apiRequest('/auth/login', 'POST', {
  nombreUsuario: 'user',
  contrasena: 'pass'
});
localStorage.setItem('token', token);

// Con MFA
const { token: preAuth, mfaRequired } = await apiRequest('/auth/login', 'POST', {...});
if (mfaRequired) {
  const { token } = await apiRequest('/auth/verify-mfa', 'POST', {
    nombreUsuario: 'user',
    totpCode: '123456',
    preAuthToken: preAuth
  });
  localStorage.setItem('token', token);
}
```

---

## Clientes (`/clientes`)

```javascript
// Listar
const clientes = await apiRequest('/clientes');

// Buscar por ID
const cliente = await apiRequest(`/clientes/${id}`);

// Buscar avanzado
const resultado = await apiRequest('/clientes/buscar?telefono=3001234567');
// ?documento=123456789 o ?nombre=Juan

// Crear
const nuevo = await apiRequest('/clientes', 'POST', {
  nombre: 'Juan',
  apellido: 'Perez',
  documento: '1234567890',  // 8-10 dígitos
  telefono: '3001234567'    // 10 dígitos, inicia con 3
});

// Actualizar
const actualizado = await apiRequest(`/clientes/${id}`, 'PUT', { telefono: '...' });

// Eliminar
await apiRequest(`/clientes/${id}`, 'DELETE'); // 204 No Content
```

---

## Empleados (`/empleados`) - Solo OWNER

```javascript
// Listar
const empleados = await apiRequest('/empleados');

// Crear
const nuevo = await apiRequest('/empleados', 'POST', {
  nombre: 'Ana',
  apellido: 'López',
  correo: 'ana@salon.com',
  telefono: '3201234567',
  franjasLaborales: [
    { dia: 'MONDAY', horaInicio: '08:00', horaFin: '16:00' },
    { dia: 'WEDNESDAY', horaInicio: '08:00', horaFin: '16:00' }
  ],
  idsServicios: [1, 3, 5]  // Servicios que puede realizar
});

// Días válidos: MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
// Formato hora: "HH:mm" (24h)

// Actualizar
await apiRequest(`/empleados/${id}`, 'PUT', { telefono: '...' });

// Eliminar (soft delete)
await apiRequest(`/empleados/${id}`, 'DELETE');
```

---

## Servicios (`/servicios`)

```javascript
// Listar
const servicios = await apiRequest('/servicios');

// Buscar por nombre
const resultado = await apiRequest('/servicios/buscar?nombre=Corte');

// Crear
const nuevo = await apiRequest('/servicios', 'POST', {
  nombre: 'Corte de Cabello',
  duracion: 30,  // minutos
  recursosRequeridos: [
    { idRecurso: 1, cantidadRequerida: 1 },
    { idRecurso: 5, cantidadRequerida: 1 }
  ]
});

// Actualizar
await apiRequest(`/servicios/${id}`, 'PUT', { duracion: 45 });

// Eliminar
await apiRequest(`/servicios/${id}`, 'DELETE');
```

---

## Recursos (`/recursos`)

```javascript
// Listar
const recursos = await apiRequest('/recursos');

// Crear
const nuevo = await apiRequest('/recursos', 'POST', {
  nombre: 'Tijeras Profesionales',
  cantidadTotal: 10
});

// Actualizar
await apiRequest(`/recursos/${id}`, 'PUT', { cantidadTotal: 15 });

// Eliminar
await apiRequest(`/recursos/${id}`, 'DELETE');
```

---

## Citas (`/citas`)

```javascript
// Listar todas
const citas = await apiRequest('/citas');

// Buscar por ID
const cita = await apiRequest(`/citas/${idCita}`);

// Crear (con validaciones automáticas)
const nueva = await apiRequest('/citas', 'POST', {
  idCliente: 1,
  idEmpleado: 2,
  idServicio: 5,
  fechaInicio: '2026-02-15T14:00:00'  // ISO 8601
});

// Validaciones automáticas al crear:
// ✅ Empleado activo y calificado
// ✅ Franja laboral disponible
// ✅ Sin solapamiento
// ✅ Recursos disponibles
// ✅ Fecha futura

// Actualizar (cambiar fecha o estado)
await apiRequest(`/citas/${idCita}`, 'PUT', {
  estado: 'REALIZADA'  // CONFIRMADA, CANCELADA, REALIZADA
});

// Eliminar
await apiRequest(`/citas/${idCita}`, 'DELETE');
```

---

## Usuarios (`/usuarios`) - Solo OWNER

```javascript
// Listar
const usuarios = await apiRequest('/usuarios');

// Crear
const nuevo = await apiRequest('/usuarios', 'POST', {
  nombreUsuario: 'recepcionista01',
  contrasena: 'Password123!',
  rol: 'RECEPTIONIST',  // OWNER o RECEPTIONIST
  idEmpleado: 5         // Relación 1:1 (cada empleado puede tener máximo un usuario)
});
// Nota: Los usuarios OWNER tienen MFA habilitado automáticamente

// Actualizar
await apiRequest(`/usuarios/${id}`, 'PUT', { contrasena: 'NewPass456!' });

// Desactivar (soft delete)
await apiRequest(`/usuarios/${id}`, 'DELETE');
```

---

## Disponibilidad (`/disponibilidad`)

```javascript
// Consultar horarios disponibles para una fecha (todos los empleados)
const disponibles = await apiRequest(
  '/disponibilidad?fecha=2026-02-15&idServicio=1'
);

// Consultar disponibilidad de un empleado específico
const disponiblesEmpleado = await apiRequest(
  '/disponibilidad?fecha=2026-02-15&idServicio=1&idEmpleado=3'
);

// Response:
// {
//   "fecha": "2026-02-15",
//   "horariosDisponibles": ["09:00:00", "10:00:00", "11:00:00", "14:00:00", "15:00:00"]
// }
```

---

## Manejo de Errores

### Estructura de Error

```json
{
  "timestamp": "2026-02-09T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Descripción del error",
  "path": "/api/v1/clientes"
}
```

### Códigos HTTP

| Código | Descripción | Cuándo |
|--------|-------------|--------|
| 200 | OK | GET, PUT exitosos |
| 201 | Created | POST exitoso |
| 204 | No Content | DELETE exitoso |
| 400 | Bad Request | Validación fallida |
| 401 | Unauthorized | Token inválido/expirado |
| 403 | Forbidden | Sin permisos (ej: RECEPTIONIST accediendo a /usuarios) |
| 404 | Not Found | Recurso no existe |
| 409 | Conflict | Recurso duplicado, solapamiento de citas |
| 500 | Server Error | Error interno |

### Manejo en Frontend

```javascript
try {
  const data = await apiRequest('/endpoint', 'POST', {...});
} catch (error) {
  switch (error.message) {
    case 'Sesión expirada':
      // Redirigir a login
      window.location.href = '/login';
      break;
    default:
      alert('Error: ' + error.message);
  }
}
```

---

## Mejores Prácticas

### 1. Validaciones Frontend

```javascript
// Teléfono colombiano (10 dígitos, inicia con 3)
const validarTelefono = (tel) => /^3\d{9}$/.test(tel);

// Documento (8-10 dígitos)
const validarDocumento = (doc) => /^\d{8,10}$/.test(doc);

// Validar antes de enviar
if (!validarTelefono(telefono)) {
  alert('Teléfono inválido');
  return;
}
```

### 2. Formateo de Fechas

```javascript
// Para enviar a API (ISO 8601)
const formatearParaAPI = (fecha, hora) => `${fecha}T${hora}:00`;
// Uso: formatearParaAPI('2026-02-15', '14:00') → '2026-02-15T14:00:00'

// Para mostrar al usuario
const formatearParaMostrar = (fechaISO) =>
  new Date(fechaISO).toLocaleString('es-CO', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
// Uso: '2026-02-15T14:00:00' → '15 de febrero de 2026, 2:00 p. m.'
```

### 3. Loading States

```javascript
let isLoading = false;

function setLoading(loading) {
  isLoading = loading;
  document.getElementById('spinner').style.display = loading ? 'block' : 'none';
}

async function fetchConLoading(promesa) {
  setLoading(true);
  try {
    return await promesa;
  } finally {
    setLoading(false);
  }
}

// Uso
const clientes = await fetchConLoading(apiRequest('/clientes'));
```

### 4. Logout

```javascript
function logout() {
  localStorage.removeItem('token');
  window.location.href = '/login';
}
```

---

## Ejemplo Completo: Crear Cita

```javascript
// 1. Cargar datos iniciales
const clientes = await apiRequest('/clientes');
const servicios = await apiRequest('/servicios');

// 2. Al seleccionar servicio, cargar empleados calificados
const empleados = await apiRequest('/empleados');
const empleadosCalificados = empleados.filter(emp =>
  emp.calificaciones.some(cal => cal.idServicio == servicioId)
);

// 3. Consultar disponibilidad (opcional)
const disponibles = await apiRequest(
  `/disponibilidad?fecha=2026-02-15&idServicio=${servicioId}`
);

// 4. Crear cita
try {
  const cita = await apiRequest('/citas', 'POST', {
    idCliente: 1,
    idEmpleado: 2,
    idServicio: 5,
    fechaInicio: '2026-02-15T14:00:00'
  });
  alert('Cita creada exitosamente');
  console.log(cita);
} catch (error) {
  if (error.message.includes('solapamiento')) {
    alert('El empleado ya tiene una cita en ese horario');
  } else if (error.message.includes('recursos')) {
    alert('No hay recursos disponibles');
  } else {
    alert('Error: ' + error.message);
  }
}
```

---

## Recursos Adicionales

- **Swagger UI:** http://localhost:8080/swagger-ui.html (prueba interactiva)
- **OpenAPI Spec:** http://localhost:8080/v3/api-docs
- **Documentación Técnica:** [DOCUMENTACION.md](DOCUMENTACION.md)

---

**v1.0** • Última actualización: Febrero 2026
