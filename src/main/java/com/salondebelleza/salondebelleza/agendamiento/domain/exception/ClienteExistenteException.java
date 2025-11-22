package com.salondebelleza.salondebelleza.agendamiento.domain.exception;

public class ClienteExistenteException extends RuntimeException {
    public ClienteExistenteException(String message) {
        super(message);
    }
}
