package com.challenge.technical.catalog_service.model;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;

public record Product(
    @Id Long id,
    String name,
    String description,
    BigDecimal price,
    int stock   
) {}
