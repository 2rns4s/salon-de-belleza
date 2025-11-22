package com.salondebelleza.salondebelleza.agendamiento.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ActualizarClienteRequest(
        @Size(min = 3, max = 100, message = "El nombre debe tener una longitud de 3 a 100 caracteres.")
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @Size(min = 3, max = 100, message = "El apellido debe tener una longitud de 3 a 100 caracteres.")
        @NotBlank(message = "El apellido es obligatorio")
        String apellido,

        @NotBlank(message = "El telefono es obligatorio")
        @Pattern(
                regexp = "^3\\d{9}$",
                message = "El celular debe tener 10 dígitos y empezar por 3 (formato nacional colombiano, sin +57).")
        String telefono
) {
}
