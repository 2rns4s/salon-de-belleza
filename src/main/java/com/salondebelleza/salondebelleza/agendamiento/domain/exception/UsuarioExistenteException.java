package com.salondebelleza.salondebelleza.agendamiento.domain.exception;

public class UsuarioExistenteException extends RuntimeException {
    public UsuarioExistenteException(String message) {
        super(message);
    }
}
