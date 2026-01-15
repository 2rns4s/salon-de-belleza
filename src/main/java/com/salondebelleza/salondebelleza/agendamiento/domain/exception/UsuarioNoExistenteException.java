package com.salondebelleza.salondebelleza.agendamiento.domain.exception;

public class UsuarioNoExistenteException extends RuntimeException {
    public UsuarioNoExistenteException(String message) {
        super(message);
    }
}
