package com.salondebelleza.salondebelleza.agendamiento.domain.exception;

public class EmpleadoConUsuarioYaExistenteException extends RuntimeException {
    public EmpleadoConUsuarioYaExistenteException(String message) {
        super(message);
    }
}
