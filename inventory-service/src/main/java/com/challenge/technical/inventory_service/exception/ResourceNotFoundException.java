package com.challenge.technical.inventory_service.exception;

public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException() {
        super("Resource not found");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Convenience constructor to build a consistent not-found message.
     *
     * Example: new ResourceNotFoundException("Product", "id", 42)
     * produces: "Product not found with id : '42'"
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, String.valueOf(fieldValue)));
    }
}