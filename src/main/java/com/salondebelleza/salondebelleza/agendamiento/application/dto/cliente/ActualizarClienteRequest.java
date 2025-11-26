package com.salondebelleza.salondebelleza.agendamiento.application.dto.cliente;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos permitidos para la actulizacion del cliente. No se permite actualizar documento e ID")
public record ActualizarClienteRequest(

        @Schema(description = "Nombre del cliente", example = "Juan")
        @Size(min = 3, max = 100, message = "El nombre debe tener una longitud de 3 a 100 caracteres.")
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @Schema(description = "Apellido del cliente", example = "Perez")
        @Size(min = 3, max = 100, message = "El apellido debe tener una longitud de 3 a 100 caracteres.")
        @NotBlank(message = "El apellido es obligatorio")
        String apellido,

        @Schema(description = "Telefono de cliente", example = "3000000000")
        @NotBlank(message = "El telefono es obligatorio")
        @Pattern(
                regexp = "^3\\d{9}$",
                message = "El celular debe tener 10 dígitos y empezar por 3 (formato nacional colombiano, sin +57).")
        String telefono
) {
}
