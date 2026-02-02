package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web.exception;

import com.salondebelleza.salondebelleza.agendamiento.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CitaFueraDeHorarioException.class)
    public ResponseEntity<ErrorResponse> handleCitaFueraDeHorarioException(CitaFueraDeHorarioException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(EmpleadoNoTrabajaEnEseHorarioException.class)
    public ResponseEntity<ErrorResponse> handleEmpleadoNoTrabajaEnEseHorarioException(EmpleadoNoTrabajaEnEseHorarioException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(ClienteNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleClienteNoEncontradoException(ClienteNoEncontradoException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(ClienteExistenteException.class)
    public ResponseEntity<ErrorResponse> handleClienteExistenteException(ClienteExistenteException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(CitaExistenteException.class)
    public ResponseEntity<ErrorResponse> handleCitaExistenteException(CitaExistenteException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(CitaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleCitaNoEncontradaException(CitaNoEncontradaException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(UsuarioExistenteException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioExistenteException(UsuarioExistenteException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(UsuarioNoExistenteException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioNoExistenteException(UsuarioNoExistenteException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<ErrorResponse> handleAccesoDenegadoException(AccesoDenegadoException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), null);
    }

    @ExceptionHandler(EmpleadoExistenteException.class)
    public ResponseEntity<ErrorResponse> handleEmpleadoExistenteException(EmpleadoExistenteException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(EmpleadoNoExistenteException.class)
    public ResponseEntity<ErrorResponse> handleEmpleadoNoExistenteException(EmpleadoNoExistenteException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(ServicioNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleServicioNoEncontradoException(ServicioNoEncontradoException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(ServicioExistenteException.class)
    public ResponseEntity<ErrorResponse> handleServicioExistenteException(ServicioExistenteException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoNoEncontradoException(RecursoNoEncontradoException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(RecursoExistenteException.class)
    public ResponseEntity<ErrorResponse> handleRecursoExistenteException(RecursoExistenteException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(SolapamientoException.class)
    public ResponseEntity<ErrorResponse> handleSolapamientoException(SolapamientoException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(error.getField() + ": " + error.getDefaultMessage());
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Error en los datos enviados", details);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ex.printStackTrace();
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrio un error inesperado", null);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, List<String> details) {
        ErrorResponse errorResponse = new ErrorResponse(
                java.time.LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                details
        );
        return new ResponseEntity<>(errorResponse, status);
    }

}
