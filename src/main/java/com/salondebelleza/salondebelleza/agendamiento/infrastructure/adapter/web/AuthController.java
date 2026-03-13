package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.auth.LoginRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.auth.LoginResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.auth.MfaVerificationRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.usecase.auth.LoginUseCase;
import com.salondebelleza.salondebelleza.agendamiento.application.usecase.auth.VerificarMfaUseCase;
import com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base-path}/auth")
@Tag(name = "Autenticación", description = "API para la autenticación de usuarios con soporte para MFA (Multi-Factor Authentication)")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final VerificarMfaUseCase verificarMfaUseCase;

    @Autowired
    public AuthController(LoginUseCase loginUseCase, VerificarMfaUseCase verificarMfaUseCase) {
        this.loginUseCase = loginUseCase;
        this.verificarMfaUseCase = verificarMfaUseCase;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesión - Paso 1: Autenticación con credenciales",
            description = "Autentica a un usuario utilizando su nombre de usuario y contraseña. " +
                    "Este endpoint maneja dos flujos de autenticación según el rol del usuario y la configuración de MFA:\n\n" +
                    "**Flujo sin MFA (usuarios RECEPTIONIST sin MFA habilitado):**\n" +
                    "- Si el usuario es RECEPTIONIST y NO tiene MFA habilitado (`mfaEnabled = false`), retorna inmediatamente un token de acceso JWT válido.\n" +
                    "- El campo `mfaRequired` será `false`.\n" +
                    "- El token puede ser usado directamente en el header `Authorization: Bearer {token}`.\n\n" +
                    "**Flujo con MFA (usuarios OWNER o cualquier usuario con MFA habilitado):**\n" +
                    "- Si el usuario es OWNER (MFA obligatorio por defecto) o cualquier usuario que tenga `mfaEnabled = true`, retorna un token de pre-autenticación temporal.\n" +
                    "- El campo `mfaRequired` será `true`.\n" +
                    "- Este token debe ser usado junto con el código TOTP en el endpoint `/verify-mfa` para completar la autenticación.\n\n" +
                    "**Validación de usuarios inactivos:**\n" +
                    "Los usuarios con `activo = false` NO podrán autenticarse. Spring Security valida automáticamente el estado del usuario.\n\n" +
                    "**Nota:** El token de pre-autenticación tiene una duración limitada (5 minutos) y solo puede ser usado para verificación MFA."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Autenticación exitosa. La respuesta varía según si el usuario requiere MFA:\n" +
                            "- **Sin MFA:** Retorna token de acceso completo listo para usar\n" +
                            "- **Con MFA:** Retorna token de pre-autenticación que debe ser verificado con código TOTP",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida. Los datos proporcionados no cumplen con las validaciones requeridas:\n" +
                            "- Nombre de usuario vacío o nulo\n" +
                            "- Contraseña vacía o nula\n" +
                            "- Formato de datos incorrecto",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciales inválidas o usuario no autorizado. Puede ocurrir por:\n" +
                            "- Nombre de usuario no existe en el sistema\n" +
                            "- Contraseña incorrecta\n" +
                            "- Usuario inactivo (`activo = false`). Los usuarios desactivados no pueden iniciar sesión",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor durante el proceso de autenticación",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<LoginResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciales del usuario para iniciar sesión. Debe incluir nombre de usuario y contraseña válidos.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequest.class)
                    )
            )
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(this.loginUseCase.execute(request));
    }

    @PostMapping("/verify-mfa")
    @Operation(
            summary = "Verificar MFA - Paso 2: Validación de código TOTP",
            description = "Completa el proceso de autenticación multifactor verificando el código TOTP (Time-based One-Time Password) " +
                    "generado por la aplicación de autenticación del usuario (Google Authenticator, Microsoft Authenticator, Authy, etc.).\n\n" +
                    "**Flujo de verificación:**\n" +
                    "1. El usuario debe haber recibido un token de pre-autenticación del endpoint `/login` (cuando `mfaRequired = true`)\n" +
                    "2. El usuario genera un código TOTP de 6 dígitos desde su aplicación de autenticación\n" +
                    "3. Envía el código junto con el nombre de usuario y el token de pre-autenticación\n" +
                    "4. Si el código es válido, recibe un token de acceso JWT completo\n\n" +
                    "**Validaciones realizadas:**\n" +
                    "- El token de pre-autenticación debe ser válido y no estar expirado (5 minutos de duración)\n" +
                    "- El token debe ser de tipo PRE_AUTH (no se aceptan tokens de acceso completos)\n" +
                    "- El usuario debe existir y tener MFA configurado (campo `mfaSecret` no nulo)\n" +
                    "- El código TOTP debe ser válido para el secret MFA del usuario\n" +
                    "- El código debe tener exactamente 6 dígitos numéricos\n\n" +
                    "**Importante:** Los códigos TOTP son válidos por 30 segundos. Si el código expira, se debe generar uno nuevo desde la aplicación de autenticación."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Verificación MFA exitosa. Retorna un token de acceso JWT completo que puede ser usado " +
                            "para autenticar todas las solicitudes subsiguientes. El campo `mfaRequired` será `false` " +
                            "indicando que la autenticación está completa.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida. Los datos proporcionados no cumplen con las validaciones:\n" +
                            "- Nombre de usuario vacío o nulo\n" +
                            "- Código TOTP vacío, nulo o con formato incorrecto (debe ser exactamente 6 dígitos)\n" +
                            "- Token de pre-autenticación vacío o nulo\n" +
                            "- Formato de datos incorrecto",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Verificación fallida. Puede ocurrir por varios motivos:\n" +
                            "- Código TOTP incorrecto o expirado (válido por 30 segundos)\n" +
                            "- Token de pre-autenticación inválido o expirado (válido por 5 minutos)\n" +
                            "- Tipo de token incorrecto (debe ser PRE_AUTH, no un token de acceso completo)\n" +
                            "- Usuario no encontrado en el sistema\n" +
                            "- Usuario no tiene MFA configurado (falta mfaSecret)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor durante el proceso de verificación MFA",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<LoginResponse> verifyMfa(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos necesarios para verificar el MFA:\n" +
                            "- **nombreUsuario:** El nombre de usuario que está intentando autenticarse\n" +
                            "- **totpCode:** Código de 6 dígitos generado por la aplicación de autenticación (ej: 123456)\n" +
                            "- **preAuthToken:** Token temporal recibido del endpoint `/login` cuando `mfaRequired` es `true`",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MfaVerificationRequest.class)
                    )
            )
            @Valid @RequestBody MfaVerificationRequest request) {
        return ResponseEntity.ok(verificarMfaUseCase.execute(request));
    }
}
