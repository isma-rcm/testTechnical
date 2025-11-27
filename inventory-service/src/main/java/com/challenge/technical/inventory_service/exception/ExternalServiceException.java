package com.challenge.technical.inventory_service.exception;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message) { super(message); }
}