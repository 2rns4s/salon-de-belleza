package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.servicio.CrearServicioRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.servicio.ServicioResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.usecase.servicio.*;
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
@RequestMapping("${api.base-path}/servicios")
@Tag(name = "Servicios", description = "API para la gestión de servicios del salón de belleza")
public class ServicioController {

    private final ActualizarServicioUseCase actualizarServicioUseCase;
    private final BuscarServicioUseCase buscarServicioUseCase;
    private final CrearServicioUseCase crearServicioUseCase;
    private final EliminarServicioUseCase eliminarServicioUseCase;
    private final ListarServicioUseCase listarServicioUseCase;

    @Autowired
    public ServicioController(ActualizarServicioUseCase actualizarServicioUseCase, BuscarServicioUseCase buscarServicioUseCase, CrearServicioUseCase crearServicioUseCase, EliminarServicioUseCase eliminarServicioUseCase, ListarServicioUseCase listarServicioUseCase) {
        this.actualizarServicioUseCase = actualizarServicioUseCase;
        this.buscarServicioUseCase = buscarServicioUseCase;
        this.crearServicioUseCase = crearServicioUseCase;
        this.eliminarServicioUseCase = eliminarServicioUseCase;
        this.listarServicioUseCase = listarServicioUseCase;
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los servicios",
            description = "Retorna una lista completa de todos los servicios registrados en el sistema del salón de belleza. " +
                    "Los servicios pueden incluir cortes, peinados, manicuras, pedicuras y otros tratamientos de belleza."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de servicios obtenida exitosamente. La lista puede estar vacía si no hay servicios registrados.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ServicioResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar obtener la lista de servicios",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<ServicioResponse>> listarServicios(){
        return ResponseEntity.status(HttpStatus.OK).body(this.listarServicioUseCase.execute());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar servicio por ID",
            description = "Busca y retorna un servicio específico utilizando su identificador único. " +
                    "El ID debe ser un número entero positivo válido correspondiente a un servicio existente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Servicio encontrado exitosamente y retornado en la respuesta",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServicioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Servicio no encontrado. No existe un servicio con el ID proporcionado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar buscar el servicio",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ServicioResponse> buscarServicioPorId(
            @Parameter(description = "ID único del servicio a buscar", required = true, example = "1")
            @PathVariable long id){
        return ResponseEntity.status(HttpStatus.OK).body(this.buscarServicioUseCase.executeById(id));
    }

    @GetMapping("/buscar")
    @Operation(
            summary = "Buscar servicio por nombre",
            description = "Busca y retorna un servicio específico utilizando su nombre de forma exacta (no parcial) pero insensible a mayúsculas/minúsculas. " +
                    "Por ejemplo: 'corte de cabello', 'CORTE DE CABELLO' y 'Corte De Cabello' encontrarán el mismo servicio. " +
                    "El nombre debe coincidir completamente con un servicio existente en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Servicio encontrado exitosamente y retornado en la respuesta",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServicioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Servicio no encontrado. No existe un servicio con el nombre proporcionado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar buscar el servicio",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ServicioResponse> buscarServicioPorNombre(
            @Parameter(description = "Nombre del servicio a buscar", required = true, example = "Corte de cabello")
            @RequestParam String nombre){
        return ResponseEntity.status(HttpStatus.OK).body(this.buscarServicioUseCase.executeByNombre(nombre));
    }

    @PostMapping
    @Operation(
            summary = "Agregar nuevo servicio",
            description = "Registra un nuevo servicio en el sistema. Este endpoint valida que los datos proporcionados sean correctos " +
                    "y que no exista otro servicio con el mismo nombre. Todos los campos requeridos deben ser proporcionados " +
                    "según las reglas de validación definidas en el modelo CrearServicioRequest."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Servicio creado exitosamente. Retorna toda la información completa del servicio guardado incluyendo el ID asignado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServicioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o incompletos. Puede ocurrir por: nombre demasiado corto, precio negativo o duración inválida.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto: Ya existe un servicio con el mismo nombre",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar crear el servicio",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ServicioResponse> agregarServicio(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo servicio a crear. Todos los campos requeridos deben estar presentes y cumplir con las validaciones.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CrearServicioRequest.class)
                    )
            )
            @Valid @RequestBody CrearServicioRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.crearServicioUseCase.execute(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar información del servicio",
            description = "Actualiza los datos de un servicio existente identificado por su ID. Este endpoint permite modificar " +
                    "la información del servicio validando que los nuevos datos sean correctos y que no genere conflictos."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Servicio actualizado exitosamente. Retorna la información completa y actualizada del servicio.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServicioResponse.class)
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
                    description = "No se encontró servicio con el ID especificado para actualizar",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto: El nombre que intenta asignar ya está registrado en el sistema.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar actualizar servicio.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ServicioResponse> actualizarServicio(
            @Parameter(
                    description = "ID único del servicio a actualizar",
                    required = true,
                    example = "1"
            )
            @PathVariable long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del servicio. Todos los campos (nombre, descripcion, precio, duracion, recursosIds) son obligatorios. " +
                            "Se valida que el nuevo nombre no esté siendo usado por otro servicio.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CrearServicioRequest.class)
                    )
            )
            @Valid @RequestBody CrearServicioRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(this.actualizarServicioUseCase.execute(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar servicio",
            description = "Elimina un servicio específico del sistema utilizando su identificador único. " +
                    "Esta operación es irreversible y eliminará toda la información asociada al servicio. " +
                    "Una vez eliminado, no se retorna ningún contenido (HTTP 204 No Content)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Servicio eliminado exitosamente. No se retorna contenido en la respuesta",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró ningún servicio con el ID especificado para eliminar",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar eliminar el servicio",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> eliminarServicio(
            @Parameter(description = "ID único del servicio a eliminar", required = true, example = "1")
            @PathVariable long id){
        this.eliminarServicioUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
