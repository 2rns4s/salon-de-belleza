package com.salondebelleza.salondebelleza.agendamiento.application.dto.usuario;

public record UsuarioResponse(
        Long idUsuario,
        String nombreUsuario,
        String rol,
        Long idEmpleado,
        Boolean activo,
        Boolean mfaEnabled
) {
}
