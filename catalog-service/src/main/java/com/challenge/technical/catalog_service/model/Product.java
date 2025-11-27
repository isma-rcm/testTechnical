package com.challenge.technical.catalog_service.model;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;

public record Product(
    @Id Long id,
    String name,
    String description,
    BigDecimal price,
    int stock   
) {

    public Product(String name, String description, BigDecimal price, int stock) {
        this(null, name, description, price, stock); 
    }
    @PersistenceCreator
    public Product {}
    public Product newProduct(String name, String description, BigDecimal price, int stock) {
        return new Product(name, description, price, stock);
    }
}
