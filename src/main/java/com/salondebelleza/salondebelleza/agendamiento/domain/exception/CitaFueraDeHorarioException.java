package com.salondebelleza.salondebelleza.agendamiento.domain.exception;

public class CitaFueraDeHorarioException extends RuntimeException {
    public CitaFueraDeHorarioException(String message) {
        super(message);
    }
}
