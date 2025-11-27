package com.challenge.technical.inventory_service.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.challenge.technical.inventory_service.exception.ResourceNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        // Devuelve el código 404 (NOT_FOUND) con el mensaje de error.
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body("Recurso no encontrado" );
    }
}
