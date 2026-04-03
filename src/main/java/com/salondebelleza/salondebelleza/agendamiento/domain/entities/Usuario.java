package com.salondebelleza.salondebelleza.agendamiento.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Usuario {
    private Long idUsuario;
    private String nombreUsuario;
    private String contrasena;
    private String rol;
    private Long idEmpleado;
    private Boolean mfaEnabled;
    private String mfaSecret;
    private String codigosRecuperacion;
    private Boolean activo;
}
