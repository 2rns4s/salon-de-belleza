package com.salondebelleza.salondebelleza.agendamiento.domain.exception;

public class EmpleadoNoExistenteException extends RuntimeException {
    public EmpleadoNoExistenteException(String message) {
        super(message);
    }
}
