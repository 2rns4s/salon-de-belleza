package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.recursos.ActualizarRecursoRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.recursos.CrearRecursoRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.recursos.RecursoResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.usecase.recurso.*;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("${api.base-path}/recursos")
@Tag(name = "Recursos", description = "API para la gestión de recursos del salón de belleza")
public class RecursoController {

    private final ActualizarRecursoUseCase actualizarRecursoUseCase;
    private final BuscarRecursoUseCase buscarRecursoUseCase;
    private final CrearRecursoUseCase crearRecursoUseCase;
    private final EliminarRecursoUseCase eliminarRecursoUseCase;
    private final ListarRecursoUseCase listarRecursoUseCase;

    @Autowired
    public RecursoController(ActualizarRecursoUseCase actualizarRecursoUseCase, BuscarRecursoUseCase buscarRecursoUseCase, CrearRecursoUseCase crearRecursoUseCase, EliminarRecursoUseCase eliminarRecursoUseCase, ListarRecursoUseCase listarRecursoUseCase) {
        this.actualizarRecursoUseCase = actualizarRecursoUseCase;
        this.buscarRecursoUseCase = buscarRecursoUseCase;
        this.crearRecursoUseCase = crearRecursoUseCase;
        this.eliminarRecursoUseCase = eliminarRecursoUseCase;
        this.listarRecursoUseCase = listarRecursoUseCase;
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los recursos",
            description = "Retorna una lista completa de todos los recursos registrados en el sistema del salón de belleza. " +
                    "Los recursos pueden incluir equipos, herramientas y otros elementos necesarios para los servicios."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de recursos obtenida exitosamente. La lista puede estar vacía si no hay recursos registrados.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RecursoResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar obtener la lista de recursos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<RecursoResponse>> listarRecursos() {
        return ResponseEntity.status(HttpStatus.OK).body(this.listarRecursoUseCase.executeByAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar recurso por ID",
            description = "Busca y retorna un recurso específico utilizando su identificador único. " +
                    "El ID debe ser un número entero positivo válido correspondiente a un recurso existente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Recurso encontrado exitosamente y retornado en la respuesta",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RecursoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Recurso no encontrado. No existe un recurso con el ID proporcionado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar buscar el recurso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<RecursoResponse> buscarRecursoPorId(
            @Parameter(description = "ID único del recurso a buscar", required = true, example = "1")
            @PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.buscarRecursoUseCase.executeById(id));
    }

    @GetMapping("/buscar")
    @Operation(
            summary = "Buscar recurso por nombre",
            description = "Busca y retorna un recurso específico utilizando su nombre. " +
                    "La búsqueda puede ser sensible a mayúsculas/minúsculas dependiendo de la configuración del sistema. " +
                    "El nombre debe coincidir exactamente con un recurso existente en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Recurso encontrado exitosamente y retornado en la respuesta",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RecursoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Recurso no encontrado. No existe un recurso con el nombre proporcionado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar buscar el recurso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<RecursoResponse> buscarRecursoPorNombre(
            @Parameter(description = "Nombre del recurso a buscar", required = true, example = "Secador de pelo")
            @RequestParam String nombre) {
        return ResponseEntity.status(HttpStatus.OK).body(this.buscarRecursoUseCase.executeByNombre(nombre));
    }

    @PostMapping
    @Operation(summary = "Agregar nuevo recurso",
            description = "Registra un nuevo recurso cliente en el sistema. Este endpoint valida que los datos proporcionados sean correctos." +
                    "y que no exista otro recurso con el mismo nombre o la cantidad sea menor a 1. Todos los campos requeridos deben ser proporcionados " +
                    "segun las reglas de validación definidas en el modelo CrearRecursoRequest")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Recurso creado exitosamente. Retorna toda la información completa del recurso guardado incluyendo el ID asignado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecursoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o incompletos. Puede ocurrir por: cantidad menor a 1 o nombre demasiado corto.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto: Ya existe un recurso con el mismo nombre",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar crear el recurso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<RecursoResponse> agregarRecurso(@Valid @RequestBody CrearRecursoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.crearRecursoUseCase.execute(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar información del recurso",
            description = "Actualiza los datos de un recurso existente identificado por su ID. Este endpoint permite modificar " +
                    "la información del recurso validando que los nuevos datos sean correctos y que no genere conflictos "
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Recurso actualizado exitosamente. Retorna la información completa y actualizada del recurso.",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = RecursoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o incompletos. Los datos proporcionados no cumplen con las validaciones requeridas.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró recurso con el ID especificado para actualizar",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto: El nombre que intenta asignar ya esta registrado en el sistema.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar actualizar recurso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<RecursoResponse> actualizarRecurso(
            @Parameter(
                    description = "ID único del recurso a actualizar",
                    required = true,
                    example = "1"
            )
            @PathVariable long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del recurso. Solo se modificarán los campos proporcionados que cumplan con las validaciones.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ActualizarRecursoRequest.class)
                    )
            )
            @Valid @RequestBody ActualizarRecursoRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(this.actualizarRecursoUseCase.execute(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Elimnar recurso",
            description = "Elimina un recurso específico del sistema utilizando su identificador único. " +
                    "Esta operación es irreversible y eliminará toda la información asociada al recurso." +
                    "Una vez eliminado, no se retorna ningun contenido (HTTP 204 No Content)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Recurso eliminado exitosamente. No se retorna contenido en la respuesta",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró ningún recurso con el ID especificado para eliminar",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar elimninar el recurso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> eliminarRecurso(
            @Parameter(description = "ID único del recurso a eliminar", required = true, example = "1")
            @PathVariable long id) {
        this.eliminarRecursoUseCase.executeEliminar(id);
        return ResponseEntity.noContent().build();
    }
}
