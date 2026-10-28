package com.salondebelleza.salondebelleza.agendamiento.domain.exception;

public class ClienteNoExistenteException extends RuntimeException {
    public ClienteNoExistenteException(String message) {
        super(message);
    }
}
