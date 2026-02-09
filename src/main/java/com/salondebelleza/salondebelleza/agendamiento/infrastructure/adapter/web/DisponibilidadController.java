package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.disponibilidad.DisponibilidadRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.disponibilidad.DisponibilidadResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.usecase.disponibilidad.ObtenerDisponibilidadUseCase;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.base-path}/disponibilidad")
@Tag(name = "Disponibilidad", description = "API para consultar la disponibilidad de horarios para agendar citas en el salón de belleza")
public class DisponibilidadController {

    private final ObtenerDisponibilidadUseCase obtenerDisponibilidadUseCase;

    @Autowired
    public DisponibilidadController(ObtenerDisponibilidadUseCase obtenerDisponibilidadUseCase) {
        this.obtenerDisponibilidadUseCase = obtenerDisponibilidadUseCase;
    }

    @GetMapping
    @Operation(
            summary = "Obtener disponibilidad de horarios",
            description = "Consulta y retorna los horarios disponibles para agendar una cita en una fecha específica. " +
                    "Este endpoint es fundamental para el proceso de agendamiento, ya que permite al usuario visualizar " +
                    "qué horarios están libres antes de crear una cita.\n\n" +
                    "**Parámetros requeridos:**\n" +
                    "- **fecha:** La fecha para la cual se desea consultar disponibilidad (formato: YYYY-MM-DD). Debe ser presente o futura.\n" +
                    "- **idServicio:** Identificador del servicio que se desea agendar. Necesario para calcular la duración y recursos.\n\n" +
                    "**Parámetro opcional:**\n" +
                    "- **idEmpleado:** Si se especifica, filtra la disponibilidad solo para ese empleado. Si se omite, muestra disponibilidad de todos los empleados.\n\n" +
                    "**Lógica de disponibilidad:**\n" +
                    "- Se consideran las franjas laborales de los empleados\n" +
                    "- Se excluyen los horarios ya ocupados por otras citas\n" +
                    "- Se valida la disponibilidad de recursos necesarios para el servicio\n" +
                    "- Los horarios se retornan en intervalos que permiten completar el servicio antes del fin de la jornada\n\n" +
                    "**Ejemplo de uso para el frontend:**\n" +
                    "```\n" +
                    "GET /api/disponibilidad?fecha=2026-02-15&idServicio=1\n" +
                    "GET /api/disponibilidad?fecha=2026-02-15&idServicio=1&idEmpleado=3\n" +
                    "```"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Consulta de disponibilidad realizada exitosamente. Retorna la fecha consultada y una lista de horarios disponibles. " +
                            "La lista puede estar vacía si no hay horarios disponibles en la fecha especificada.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DisponibilidadResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Disponibilidad con horarios",
                                    description = "Ejemplo de respuesta con horarios disponibles",
                                    value = "{\n" +
                                            "  \"fecha\": \"2026-02-15\",\n" +
                                            "  \"horariosDisponibles\": [\n" +
                                            "    \"09:00:00\",\n" +
                                            "    \"10:00:00\",\n" +
                                            "    \"11:00:00\",\n" +
                                            "    \"14:00:00\",\n" +
                                            "    \"15:00:00\"\n" +
                                            "  ]\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error en los parámetros de entrada. Puede ocurrir por:\n" +
                            "- Fecha inválida o en formato incorrecto\n" +
                            "- Fecha en el pasado\n" +
                            "- ID de servicio ausente o inválido (debe ser positivo)\n" +
                            "- ID de empleado inválido si se proporciona (debe ser positivo)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Recurso no encontrado. Puede ocurrir si:\n" +
                            "- El servicio especificado no existe en el sistema\n" +
                            "- El empleado especificado no existe en el sistema (si se proporcionó idEmpleado)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar calcular la disponibilidad de horarios",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<DisponibilidadResponse> obtenerDisponibilidad(
            @ParameterObject @Valid DisponibilidadRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(this.obtenerDisponibilidadUseCase.execute(request));
    }
}
