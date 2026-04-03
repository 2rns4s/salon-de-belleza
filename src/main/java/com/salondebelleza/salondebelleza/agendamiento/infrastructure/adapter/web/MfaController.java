package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.auth.AuthResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.auth.MfaSetupRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.auth.MfaSetupResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.usecase.auth.GenerarMfaSetupUseCase;
import com.salondebelleza.salondebelleza.agendamiento.application.usecase.auth.HabilitarMfaUseCase;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base-path}/usuarios/me/mfa")
@RequiredArgsConstructor
@Tag(name = "Seguridad MFA", description = "Operaciones de enrolamiento de Doble Factor")
public class MfaController {

    private final GenerarMfaSetupUseCase generarMfaSetupUseCase;
    private final HabilitarMfaUseCase habilitarMfaUseCase;

    @GetMapping("/setup")
    @Operation(
            summary = "Generar configuración inicial para MFA",
            description = "Devuelve los datos para enrolar MFA del usuario autenticado: `secret` y `qrCodeUrl`. " +
                    "Frontend: mostrar QR y luego enviar `secret` + `code` a `/enable`."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Configuración MFA generada correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MfaSetupResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: se requiere token ACCESS válido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno al generar el QR para MFA",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<MfaSetupResponse> setupMfa(
            @AuthenticationPrincipal UserDetails userDetails
            ) {
        return ResponseEntity.ok(this.generarMfaSetupUseCase.execute(userDetails.getUsername()));
    }

    @PostMapping("/enable")
    @Operation(
            summary = "Habilitar MFA para el usuario autenticado",
            description = "Confirma el enrolamiento MFA validando el código TOTP. " +
                    "Si es válido, guarda `mfaEnabled = true` y `mfaSecret` para el usuario autenticado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "MFA habilitado correctamente. Retorna un nuevo token de acceso JWT con los datos del usuario actualizados.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o código MFA incorrecto/expirado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: se requiere token ACCESS válido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario autenticado no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al habilitar MFA",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<AuthResponse> habilitarMfa(
            @AuthenticationPrincipal UserDetails userDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = """
                            Payload para habilitar MFA:
                            - `secret`: recibido en `/setup`
                            - `code`: TOTP de 6 dígitos
                            """,
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MfaSetupRequest.class)
                    )
            )
            @Valid @RequestBody MfaSetupRequest request
    ){

        return ResponseEntity.status(HttpStatus.OK).body(this.habilitarMfaUseCase.execute(userDetails.getUsername(), request));
    }

}
