package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.empleado.ActualizarEmpleadoRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.empleado.CrearEmpleadoRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.empleado.EmpleadoResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.usecase.empleado.*;
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
@RequestMapping("${api.base-path}/empleados")
@Tag(name = "EmpleadoController", description = "Controlador para operaciones de empleados")
public class EmpleadoController {

    private final ActualizarEmpleadoUseCase actualizarEmpleadoUseCase;
    private final BuscarEmpleadoUseCase buscarEmpleadoUseCase;
    private final CrearEmpleadoUseCase crearEmpleadoUseCase;
    private final EliminarEmpleadoUseCase eliminarEmpleadoUseCase;
    private final ListarEmpleadoUseCase listarEmpleadoUseCase;

    @Autowired
    public EmpleadoController(ActualizarEmpleadoUseCase actualizarEmpleadoUseCase, BuscarEmpleadoUseCase buscarEmpleadoUseCase, CrearEmpleadoUseCase crearEmpleadoUseCase, EliminarEmpleadoUseCase eliminarEmpleadoUseCase, ListarEmpleadoUseCase listarEmpleadoUseCase) {
        this.actualizarEmpleadoUseCase = actualizarEmpleadoUseCase;
        this.buscarEmpleadoUseCase = buscarEmpleadoUseCase;
        this.crearEmpleadoUseCase = crearEmpleadoUseCase;
        this.eliminarEmpleadoUseCase = eliminarEmpleadoUseCase;
        this.listarEmpleadoUseCase = listarEmpleadoUseCase;
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los empleados",
            description = "Retorna una lista completa de todos los empleados registrados en el sistema del salón de belleza. " +
                    "Cada empleado incluye información personal, franjas laborales y servicios asignados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de empleados obtenida exitosamente. La lista puede estar vacía si no hay empleados registrados.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = EmpleadoResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar obtener la lista de empleados",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<EmpleadoResponse>> listarEmpleados() {
        return ResponseEntity.status(HttpStatus.OK).body(this.listarEmpleadoUseCase.execute());
    }


    @GetMapping("{id}")
    @Operation(
            summary = "Buscar empleado por ID",
            description = "Busca y retorna un empleado específico utilizando su identificador único. " +
                    "El ID debe ser un número entero positivo válido correspondiente a un empleado existente. " +
                    "La respuesta incluye toda la información del empleado, incluyendo sus franjas laborales y servicios asignados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Empleado encontrado exitosamente y retornado en la respuesta",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EmpleadoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado. No existe un empleado con el ID proporcionado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar buscar el empleado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<EmpleadoResponse> buscarEmpleadoPorId(
            @Parameter(description = "ID único del empleado a buscar", required = true, example = "1")
            @PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.buscarEmpleadoUseCase.buscarPorId(id));
    }

    @PostMapping
    @Operation(
            summary = "Agregar nuevo empleado",
            description = "Crea un nuevo empleado en el sistema del salón de belleza. " +
                    "El empleado debe tener un correo único en el sistema. " +
                    "Se deben proporcionar datos completos incluyendo información personal, franjas laborales y servicios asignados. " +
                    "El teléfono debe ser un número celular colombiano válido (10 dígitos iniciando con 3)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Empleado creado exitosamente. La respuesta contiene todos los datos del empleado creado incluyendo su ID asignado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EmpleadoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos. Los campos no cumplen con las validaciones requeridas (formato de correo, teléfono, campos obligatorios, etc.)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Uno o más servicios especificados en serviciosIds no fueron encontrados en el sistema",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto: Ya existe un empleado registrado con el correo electrónico proporcionado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar crear el empleado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<EmpleadoResponse> agregarEmpleado(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo empleado a crear. Debe incluir nombre, apellido, correo único, teléfono, franjas laborales y servicios asignados.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CrearEmpleadoRequest.class)
                    )
            )
            @Valid @RequestBody CrearEmpleadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.crearEmpleadoUseCase.execute(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar empleado",
            description = "Actualiza la información básica de un empleado existente en el sistema. " +
                    "Esta operación permite actualización parcial: solo se modificarán los campos proporcionados en el request. " +
                    "Si se proporciona un nuevo correo, debe ser único en el sistema (no debe estar ya asignado a otro empleado). " +
                    "El teléfono, si se actualiza, debe cumplir con el formato colombiano (10 dígitos iniciando con 3)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Empleado actualizado exitosamente. La respuesta contiene todos los datos actualizados del empleado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EmpleadoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos. Los campos no cumplen con las validaciones requeridas (formato de correo, teléfono, longitud de texto, etc.)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado. No existe un empleado con el ID proporcionado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto: El correo electrónico proporcionado ya está en uso por otro empleado del sistema",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar actualizar el empleado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<EmpleadoResponse> actualizarEmpleado(
            @Parameter(description = "ID único del empleado a actualizar", required = true, example = "1")
            @PathVariable long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos a actualizar del empleado. Todos los campos son opcionales, solo se actualizarán los campos proporcionados.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ActualizarEmpleadoRequest.class)
                    )
            )
            @Valid @RequestBody ActualizarEmpleadoRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(this.actualizarEmpleadoUseCase.execute(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar empleado",
            description = "Elimina un empleado del sistema utilizando su identificador único. " +
                    "Esta operación es permanente y eliminará toda la información asociada al empleado, " +
                    "incluyendo sus franjas laborales y asignaciones de servicios. " +
                    "Si el empleado tiene citas programadas activas, la eliminación podría afectar dichas citas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Empleado eliminado exitosamente. No se retorna contenido en la respuesta.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado. No existe un empleado con el ID proporcionado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar eliminar el empleado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> eliminarEmpleado(
            @Parameter(description = "ID único del empleado a eliminar", required = true, example = "1")
            @PathVariable long id) {
        this.eliminarEmpleadoUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

}
