package com.salondebelleza.salondebelleza.agendamiento.application.dto.usuario;

import jakarta.validation.constraints.*;

public record CrearUsuarioRequest(

        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 4, max = 50, message = "El usuario debe tener entre 4 y 50 caracteres")
        String nombreUsuario,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 100, message = "La contraseña debe tener al menos 8 caracteres")
        String contrasena,

        @NotBlank(message = "El rol es obligatorio")
        @Pattern(regexp = "OWNER|RECEPTIONIST", message = "El rol debe ser OWNER o RECEPTIONIST")
        String rol,

        @NotNull(message = "El ID del empleado es obligatorio")
        @Positive(message = "El ID del empleado debe ser mayor a 0")
        Long idEmpleado
) {
}
