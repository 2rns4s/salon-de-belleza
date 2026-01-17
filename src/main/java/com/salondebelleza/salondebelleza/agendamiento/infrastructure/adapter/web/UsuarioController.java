package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.usuario.ActualizarUsuarioRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.usuario.CrearUsuarioRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.usuario.UsuarioResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.usecase.usuario.*;
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
@RequestMapping("${api.base-path}/usuarios")
@Tag(name = "Usuarios", description = "API para la gestión de usuarios del salón de belleza")
public class UsuarioController {

    private final ActualizarUsuarioUseCase actualizarUsuarioUseCase;
    private final BuscarUsuarioUseCase buscarUsuarioUseCase;
    private final CrearUsuarioUseCase crearUsuarioUseCase;
    private final EliminarUsuarioUseCase eliminarUsuarioUseCase;
    private final ListarUsuarioUseCase listarUsuarioUseCase;

    @Autowired
    public UsuarioController(ActualizarUsuarioUseCase actualizarUsuarioUseCase, BuscarUsuarioUseCase buscarUsuarioUseCase, CrearUsuarioUseCase crearUsuarioUseCase, EliminarUsuarioUseCase eliminarUsuarioUseCase, ListarUsuarioUseCase listarUsuarioUseCase) {
        this.actualizarUsuarioUseCase = actualizarUsuarioUseCase;
        this.buscarUsuarioUseCase = buscarUsuarioUseCase;
        this.crearUsuarioUseCase = crearUsuarioUseCase;
        this.eliminarUsuarioUseCase = eliminarUsuarioUseCase;
        this.listarUsuarioUseCase = listarUsuarioUseCase;
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los usuarios",
            description = "Retorna una lista completa de todos los usuarios registrados en el sistema del salón de belleza. " +
                    "Cada usuario incluye información personal, rol asignado y estado de actividad. " +
                    "Los usuarios pueden ser clientes, empleados o administradores del sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuarios obtenida exitosamente. La lista puede estar vacía si no hay usuarios registrados.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UsuarioResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar obtener la lista de usuarios",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios(){
        return ResponseEntity.status(HttpStatus.OK).body(this.listarUsuarioUseCase.execute());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar usuario por ID",
            description = "Busca y retorna un usuario específico utilizando su identificador único. " +
                    "El ID debe ser un número entero positivo válido correspondiente a un usuario existente. " +
                    "La respuesta incluye toda la información del usuario, incluyendo sus datos personales, rol, estado y demás atributos del perfil."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado exitosamente y retornado en la respuesta",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado. No existe un usuario con el ID proporcionado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar buscar el usuario",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<UsuarioResponse> buscarUsuarioPorId(
            @Parameter(
                    name = "id",
                    description = "ID único del usuario a buscar",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(this.buscarUsuarioUseCase.execute(id));
    }

    @PostMapping
    @Operation(
            summary = "Crear nuevo usuario",
            description = "Registra un nuevo usuario en el sistema del salón de belleza. " +
                    "Este endpoint permite crear usuarios con diferentes roles (clientes, empleados, administradores) " +
                    "proporcionando todos los datos necesarios para su registro. Se validan los datos de entrada " +
                    "para garantizar la integridad de la información: unicidad del nombre de usuario, " +
                    "existencia del empleado asociado, y que el empleado no tenga ya un usuario registrado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario creado exitosamente. Retorna el usuario recién creado con su ID asignado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida. Los datos proporcionados no cumplen con las validaciones requeridas " +
                            "(campos obligatorios faltantes, formato incorrecto, datos inválidos, etc.)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto. Ya existe un usuario con el nombre de usuario proporcionado, " +
                            "el empleado no existe, o el empleado ya tiene un usuario registrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar crear el usuario",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<UsuarioResponse> crearUsuario(
            @Parameter(
                    description = "Datos del nuevo usuario a registrar. Incluye información personal, " +
                            "credenciales de acceso y rol asignado. Todos los campos marcados como obligatorios deben ser proporcionados.",
                    required = true
            )
            @Valid @RequestBody CrearUsuarioRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.crearUsuarioUseCase.execute(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar información de usuario",
            description = "Modifica los datos de un usuario existente en el sistema. " +
                    "Permite actualizar información personal, cambiar roles, modificar el estado de actividad " +
                    "y actualizar credenciales. Solo se actualizan los campos proporcionados en la solicitud. " +
                    "Si se proporciona una nueva contraseña, esta será encriptada antes de ser almacenada."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario actualizado exitosamente. Retorna el usuario con los datos modificados.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida. Los datos proporcionados no cumplen con las validaciones " +
                            "(formato incorrecto, datos inválidos, etc.)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado. No existe un usuario con el ID proporcionado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar actualizar el usuario",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<UsuarioResponse> actualizarUsuario(
            @Parameter(
                    name = "id",
                    description = "ID único del usuario a actualizar",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,
            @Parameter(
                    description = "Datos actualizados del usuario. Solo se modificarán los campos proporcionados. " +
                            "Los campos no incluidos mantendrán sus valores actuales.",
                    required = true
            )
            @Valid @RequestBody ActualizarUsuarioRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(this.actualizarUsuarioUseCase.execute(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Desactivar usuario",
            description = "Desactiva un usuario del sistema estableciendo su estado activo a false (eliminación lógica). " +
                    "Esta operación NO elimina el registro de la base de datos, sino que marca el usuario como inactivo. " +
                    "El usuario desactivado no podrá iniciar sesión, pero toda su información histórica se mantiene intacta " +
                    "(citas, calificaciones, etc.). Si el usuario ya está inactivo, la operación no realiza cambios. " +
                    "Esta es una operación segura que preserva la integridad referencial y el historial del sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuario desactivado exitosamente. Sin contenido en la respuesta. " +
                            "Si el usuario ya estaba inactivo, la operación se completa sin realizar cambios.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado. No existe un usuario con el ID proporcionado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor al intentar desactivar el usuario",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> eliminarUsuario(
            @Parameter(
                    name = "id",
                    description = "ID único del usuario a desactivar. El usuario se marcará como inactivo (activo = false)",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id){
        this.eliminarUsuarioUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
