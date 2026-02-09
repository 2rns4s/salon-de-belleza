package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.cliente.ActualizarClienteRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.cliente.ClienteResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.cliente.CrearClienteRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.usecase.cliente.*;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.base-path}/clientes")
@Tag(name = "ClienteController", description = "Controlador para el manejo de clientes")
public class ClienteController {
    private final ActualizarClienteUseCase actualizarClienteUseCase;
    private final BuscarClienteUseCase buscarClienteUseCase;
    private final CrearClienteUseCase crearClienteUseCase;
    private final EliminarClienteUseCase eliminarClienteUseCase;
    private final ListarClienteUseCase listarClienteUseCase;

    @Autowired
    public ClienteController(ActualizarClienteUseCase actualizarClienteUseCase, BuscarClienteUseCase buscarClienteUseCase, CrearClienteUseCase crearClienteUseCase, EliminarClienteUseCase eliminarClienteUseCase, ListarClienteUseCase listarClienteUseCase) {
        this.actualizarClienteUseCase = actualizarClienteUseCase;
        this.buscarClienteUseCase = buscarClienteUseCase;
        this.crearClienteUseCase = crearClienteUseCase;
        this.eliminarClienteUseCase = eliminarClienteUseCase;
        this.listarClienteUseCase = listarClienteUseCase;
    }

    @GetMapping
    @Operation(
            summary = "Obtener todos los clientes",
            description = "Obtiene la lista completa de todos los clientes registrados en el sistema. " +
                    "La lista puede estar vacía si no hay clientes registrados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de clientes obtenida exitosamente. Retorna un array que puede estar vacío si no hay clientes registrados.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ClienteResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar obtener la lista de clientes",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<ClienteResponse>> listarClientes() {
        return ResponseEntity.status(HttpStatus.OK).body(listarClienteUseCase.executeByAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar cliente por ID",
            description = "Busca y retorna la información completa de un cliente específico utilizando su identificador único (ID). " +
                    "Este endpoint es útil para obtener los detalles de un cliente conociendo su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cliente encontrado exitosamente. Retorna la información completa del cliente.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClienteResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró ningún cliente con el ID especificado en el sistema",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar buscar el cliente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ClienteResponse> buscarClientePorId(
            @Parameter(
                    description = "ID único del cliente a buscar",
                    required = true,
                    example = "1"
            )
            @PathVariable long id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(buscarClienteUseCase.executeById(id));
    }

    @GetMapping("/buscar")
    @Operation(
            summary = "Búsqueda avanzada de clientes",
            description = "Permite realizar búsquedas de clientes utilizando diferentes criterios de forma flexible. " +
                    "Este endpoint acepta tres parámetros opcionales pero al menos uno debe ser proporcionado. " +
                    "**Importante:** El tipo de retorno varía según el criterio de búsqueda:\n\n" +
                    "- **Búsqueda por teléfono:** Retorna un único objeto ClienteResponse (búsqueda exacta)\n" +
                    "- **Búsqueda por documento:** Retorna un único objeto ClienteResponse (búsqueda exacta)\n" +
                    "- **Búsqueda por nombre:** Retorna un array de ClienteResponse (búsqueda parcial)\n\n" +
                    "Si se proporcionan múltiples parámetros, se aplica el siguiente orden de prioridad: teléfono > documento > nombre"
    )
    @Parameters({
            @Parameter(
                    name = "telefono",
                    description = "Número de teléfono exacto del cliente. Realiza una búsqueda exacta y retorna un único resultado.",
                    required = false,
                    example = "3001234567"
            ),
            @Parameter(
                    name = "documento",
                    description = "Número de documento de identidad exacto del cliente. Realiza una búsqueda exacta y retorna un único resultado.",
                    required = false,
                    example = "1234567890"
            ),
            @Parameter(
                    name = "nombre",
                    description = "Nombre o parte del nombre del cliente. Realiza una búsqueda parcial (LIKE) insensible a mayúsculas/minúsculas y retorna una lista de coincidencias. " +
                            "Por ejemplo: 'juan' encontrará 'Juan Pérez', 'María Juana', 'JUAN CARLOS', etc.",
                    required = false,
                    example = "Juan"
            )
    })
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Búsqueda realizada exitosamente. El formato de respuesta varía según el criterio de búsqueda utilizado: " +
                            "objeto único para teléfono/documento, o array para nombre.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(oneOf = {ClienteResponse.class, ClienteResponse[].class})
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró ningún cliente que coincida con el criterio de búsqueda. " +
                            "Esta respuesta solo aplica para búsquedas únicas (por teléfono o documento).",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida. Debe proporcionar al menos un parámetro de búsqueda (teléfono, documento o nombre).",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar realizar la búsqueda",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> buscarCliente(
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) String nombre
    ) {
        if (telefono != null) {
            return ResponseEntity.status(HttpStatus.OK).body(this.buscarClienteUseCase.executeByTelefono(telefono));
        } else if (documento != null) {
            return ResponseEntity.status(HttpStatus.OK).body(this.buscarClienteUseCase.executeByDocumento(documento));
        } else if (nombre != null) {
            return ResponseEntity.status(HttpStatus.OK).body(this.listarClienteUseCase.executeByNombre(nombre));
        } else return ResponseEntity.badRequest().body("Debe ingresar al menos un parametro");
    }

    @PostMapping
    @Operation(
            summary = "Crear un nuevo cliente",
            description = "Registra un nuevo cliente en el sistema. Este endpoint valida que los datos proporcionados sean correctos " +
                    "y que no exista otro cliente con el mismo teléfono o documento. Todos los campos requeridos deben ser proporcionados " +
                    "según las reglas de validación definidas en el modelo CrearClienteRequest."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Cliente creado exitosamente. Retorna la información completa del cliente registrado incluyendo su ID asignado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClienteResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o incompletos. Puede ocurrir por: campos requeridos faltantes, formato de datos incorrecto, " +
                            "o valores que no cumplen con las validaciones (ej: teléfono con formato incorrecto, email inválido, etc.)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto: Ya existe un cliente registrado con el mismo teléfono o documento de identidad",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar crear el cliente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ClienteResponse> agregarCliente(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo cliente a registrar. Debe incluir todos los campos requeridos según las validaciones.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CrearClienteRequest.class)
                    )
            )
            @Valid @RequestBody CrearClienteRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.crearClienteUseCase.execute(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar información de un cliente",
            description = "Actualiza los datos de un cliente existente identificado por su ID. " +
                    "Los campos que se pueden actualizar son: nombre, apellido y teléfono. " +
                    "El documento de identidad NO se puede modificar (no está incluido en el request de actualización). " +
                    "Se valida que el nuevo teléfono no esté siendo usado por otro cliente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cliente actualizado exitosamente. Retorna la información completa del cliente con los datos actualizados.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClienteResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o incompletos. Los datos proporcionados no cumplen con las validaciones requeridas " +
                            "(ej: formato de teléfono incorrecto, email inválido, campos vacíos cuando son requeridos, etc.)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró ningún cliente con el ID especificado para actualizar",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto: El teléfono que intenta asignar ya está siendo utilizado por otro cliente en el sistema",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar actualizar el cliente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ClienteResponse> actualizarCliente(
            @Parameter(
                    description = "ID único del cliente que se desea actualizar",
                    required = true,
                    example = "1"
            )
            @PathVariable long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del cliente. Incluye nombre, apellido y teléfono. " +
                            "Todos los campos son obligatorios y deben cumplir con las validaciones. " +
                            "El documento NO se puede actualizar (no está incluido en este request).",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ActualizarClienteRequest.class)
                    )
            )
            @Valid @RequestBody ActualizarClienteRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(this.actualizarClienteUseCase.execute(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un cliente",
            description = "Elimina permanentemente un cliente del sistema utilizando su ID. Esta operación es irreversible. " +
                    "Se recomienda verificar que el cliente no tenga citas o servicios pendientes antes de eliminarlo. " +
                    "Una vez eliminado, no se retorna ningún contenido (HTTP 204 No Content)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Cliente eliminado exitosamente. No se retorna contenido en el cuerpo de la respuesta.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró ningún cliente con el ID especificado para eliminar",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar eliminar el cliente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> eliminarCliente(
            @Parameter(
                    description = "ID único del cliente que se desea eliminar del sistema",
                    required = true,
                    example = "1"
            )
            @PathVariable long id
    ) {
        this.eliminarClienteUseCase.executeById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
