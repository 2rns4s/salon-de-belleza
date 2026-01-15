package com.salondebelleza.salondebelleza.agendamiento.application.dto.usuario;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ActualizarUsuarioRequest(
        @Size(min = 4, max = 50, message = "El usuario debe tener entre 4 y 50 caracteres")
        String nombreUsuario,

        @Size(min = 8, max = 100, message = "La contraseña nueva debe tener al menos 8 caracteres")
        String contrasena,

        @Pattern(regexp = "OWNER|RECEPTIONIST", message = "El rol debe ser OWNER o RECEPTIONIST")
        String rol,

        Boolean activo
) {
}
