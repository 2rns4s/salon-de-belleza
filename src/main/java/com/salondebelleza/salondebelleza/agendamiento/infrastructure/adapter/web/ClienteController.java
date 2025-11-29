package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.cliente.ActualizarClienteRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.cliente.ClienteResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.cliente.CrearClienteRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.usecase.*;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.base-path}/clientes")
@Schema(name = "ClienteController", description = "Controlador para el manejo de clientes")
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
    @Operation(summary = "Listar todos los clientes", description = "Listar todos los clientes registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida exitosamente(Puede estar vacia)",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClienteResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ClienteResponse>> listarClientes() {
        return ResponseEntity.status(HttpStatus.OK).body(listarClienteUseCase.executeByAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por id", description = "Buscar cliente por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca cliente usando su ID",
                    content = @Content(schema = @Schema(implementation = ClienteResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado por su ID",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ClienteResponse> buscarClientePorId(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK).body(buscarClienteUseCase.executeById(id));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Busqueda avanzada de clientes",
            description = "Permite buscar clientes por diferentes criterios. " +
                    "Nota: Si busca por telefono o documento, devuelve un unico objeto. " +
                    "Si busca por nombre, devuelve una lista")
    @Parameters({
            @Parameter(name = "telefono", description = "Telefono exacto (retorna resultado unico)", required = false, example = "3000000000"),
            @Parameter(name = "documento", description = "Documento exacto (retorna resultado unico)", required = false, example = "1234567890"),
            @Parameter(name = "nombre", description = "Nombre parcial (retorna lista)", required = false, example = "Juan")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busqueda exitosa (Objeto o lista segun criterio)",
                    content = @Content(schema = @Schema(oneOf = {ClienteResponse.class, ClienteResponse[].class}))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado (Solo aplica para busquedas unicas)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Debe enviar al menos un parametro de busqueda",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<?> buscarCliente(@RequestParam(required = false) String telefono,
                                           @RequestParam(required = false) String documento,
                                           @RequestParam(required = false) String nombre) {
        if (telefono != null) {
            return ResponseEntity.status(HttpStatus.OK).body(this.buscarClienteUseCase.executeByTelefono(telefono));
        } else if (documento != null) {
            return ResponseEntity.status(HttpStatus.OK).body(this.buscarClienteUseCase.executeByDocumento(documento));
        } else if (nombre != null) {
            return ResponseEntity.status(HttpStatus.OK).body(this.listarClienteUseCase.executeByNombre(nombre));
        } else return ResponseEntity.badRequest().body("Debe ingresar al menos un parametro");
    }

    @PostMapping
    @Operation(summary = "Agregar un nuevo cliente", description = "Agregar un nuevo cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente agregado correctamente",
                    content = @Content(schema = @Schema(implementation = ClienteResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos incompletos o invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Cliente ya existente",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ClienteResponse> agregarCliente(@Valid @RequestBody CrearClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.crearClienteUseCase.execute(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un cliente", description = "Actualizar datos de un cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = ClienteResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente a actualizar no encontrado por su ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Cliente con telefono ya existente",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos incompletos o invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ClienteResponse> actualizarCliente(@PathVariable long id, @Valid @RequestBody ActualizarClienteRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(this.actualizarClienteUseCase.execute(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un cliente", description = "Eliminar un cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente eliminado correctamente",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Cliente a eliminar no encontrado por su ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servicor",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<Void> eliminarCliente(@PathVariable long id) {
        this.eliminarClienteUseCase.executeById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
