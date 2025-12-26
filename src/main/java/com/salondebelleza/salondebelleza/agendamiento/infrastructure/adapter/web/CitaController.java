package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.cita.ActualizarCitaRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.cita.CitaResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.cita.CrearCitaRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.usecase.cita.*;
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
@RequestMapping("${api.base-path}/citas")
@Tag(name = "CitaController", description = "Controlador para operaciones de citas y agendamiento de servicios")
public class CitaController {

    private final ActualizarCitaUseCase actualizarCitaUseCase;
    private final BuscarCitaUseCase buscarCitaUseCase;
    private final CrearCitaUseCase crearCitaUseCase;
    private final EliminarCitaUseCase eliminarCitaUseCase;
    private final ListarCitaUseCase listarCitaUseCase;

    @Autowired
    public CitaController(ActualizarCitaUseCase actualizarCitaUseCase, BuscarCitaUseCase buscarCitaUseCase, CrearCitaUseCase crearCitaUseCase, EliminarCitaUseCase eliminarCitaUseCase, ListarCitaUseCase listarCitaUseCase) {
        this.actualizarCitaUseCase = actualizarCitaUseCase;
        this.buscarCitaUseCase = buscarCitaUseCase;
        this.crearCitaUseCase = crearCitaUseCase;
        this.eliminarCitaUseCase = eliminarCitaUseCase;
        this.listarCitaUseCase = listarCitaUseCase;
    }

    @GetMapping("/{idCita}")
    @Operation(
            summary = "Buscar cita por ID",
            description = "Busca y retorna una cita específica utilizando su identificador único. " +
                    "El ID debe ser un número entero positivo válido correspondiente a una cita existente. " +
                    "La respuesta incluye toda la información de la cita, incluyendo cliente, servicio, empleado, fechas, estado y recursos asignados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cita encontrada exitosamente y retornada en la respuesta",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CitaResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cita no encontrada. No existe una cita con el ID proporcionado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar buscar la cita",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<CitaResponse> buscarCitaPorId(
            @Parameter(description = "ID único de la cita a buscar", required = true, example = "1")
            @PathVariable Long idCita) {
        return ResponseEntity.status(HttpStatus.OK).body(this.buscarCitaUseCase.executeById(idCita));
    }

    @GetMapping
    @Operation(
            summary = "Listar todas las citas",
            description = "Retorna una lista completa de todas las citas registradas en el sistema del salón de belleza. " +
                    "Cada cita incluye información del cliente, servicio, empleado asignado, fechas programadas, estado actual y recursos asignados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de citas obtenida exitosamente. La lista puede estar vacía si no hay citas registradas.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CitaResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar obtener la lista de citas",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<CitaResponse>> listarCitas(){
        return ResponseEntity.status(HttpStatus.OK).body(this.listarCitaUseCase.execute());
    }

    @PostMapping
    @Operation(
            summary = "Crear nueva cita",
            description = "Crea una nueva cita en el sistema del salón de belleza. " +
                    "Valida la disponibilidad del empleado en la fecha y hora solicitada, " +
                    "verifica que el empleado esté asignado al servicio solicitado, " +
                    "calcula automáticamente la hora de finalización según la duración del servicio, " +
                    "y asigna los recursos necesarios para el servicio. " +
                    "La fecha de inicio debe ser en el futuro y dentro del horario laboral del empleado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Cita creada exitosamente. La respuesta contiene todos los datos de la cita creada incluyendo su ID asignado, fecha de finalización calculada y recursos asignados.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CitaResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos. Los campos no cumplen con las validaciones requeridas (fecha en el pasado, campos obligatorios faltantes, etc.)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cliente, servicio o empleado no encontrado. Uno de los IDs proporcionados no existe en el sistema",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto: El empleado no está disponible en la fecha y hora solicitada, o no está asignado al servicio especificado, o no hay recursos suficientes disponibles",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar crear la cita",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<CitaResponse> crearCita(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la nueva cita a crear. Debe incluir cliente, servicio, empleado y fecha de inicio. La fecha debe ser en el futuro y el empleado debe estar disponible.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CrearCitaRequest.class)
                    )
            )
            @Valid @RequestBody CrearCitaRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.crearCitaUseCase.execute(request));
    }

    @PutMapping("/{idCita}")
    @Operation(
            summary = "Actualizar estado de cita",
            description = "Actualiza el estado de una cita existente en el sistema. " +
                    "Permite cambiar entre los estados: PENDIENTE, CONFIRMADA, CANCELADA, REALIZADA. " +
                    "El cambio de estado puede tener efectos en la disponibilidad de recursos y horarios del empleado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado de la cita actualizado exitosamente. La respuesta contiene todos los datos actualizados de la cita.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CitaResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos. El nuevo estado no es válido o está vacío.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cita no encontrada. No existe una cita con el ID proporcionado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar actualizar la cita",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<CitaResponse> actualizarCita(
            @Parameter(description = "ID único de la cita a actualizar", required = true, example = "1")
            @PathVariable Long idCita,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevo estado de la cita. Los estados posibles son: PENDIENTE, CONFIRMADA, CANCELADA, REALIZADA.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ActualizarCitaRequest.class)
                    )
            )
            @Valid @RequestBody ActualizarCitaRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(this.actualizarCitaUseCase.execute(idCita, request));
    }

    @DeleteMapping("/{idCita}")
    @Operation(
            summary = "Eliminar cita",
            description = "Elimina una cita del sistema del salón de belleza utilizando su ID. " +
                    "Esta operación libera los recursos asignados y el horario del empleado asociado a la cita. " +
                    "La operación es permanente y no se puede deshacer."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Cita eliminada exitosamente. No se retorna contenido en la respuesta.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cita no encontrada. No existe una cita con el ID proporcionado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar eliminar la cita",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> eliminarCita(
            @Parameter(description = "ID único de la cita a eliminar", required = true, example = "1")
            @PathVariable Long idCita) {
        this.eliminarCitaUseCase.execute(idCita);
        return ResponseEntity.noContent().build();
    }
}
