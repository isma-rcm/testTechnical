package com.challenge.technical.catalog_service.model;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
// Modelo de datos inmutable para productos
public record Product(
    @Id Long id,
    String name,
    String description,
    BigDecimal price,
    int stock   
) {
    // Constructor adicional para crear nuevos productos sin ID
    public Product(String name, String description, BigDecimal price, int stock) {
        this(null, name, description, price, stock); 
    }
    // Método para crear una nueva instancia de Product con un nuevo ID
    @PersistenceCreator
    public Product {}
    public Product newProduct(String name, String description, BigDecimal price, int stock) {
        return new Product(name, description, price, stock);
    }
}
