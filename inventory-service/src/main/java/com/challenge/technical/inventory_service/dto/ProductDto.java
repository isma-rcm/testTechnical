package com.challenge.technical.inventory_service.dto;

import java.math.BigDecimal;
// DTO inmutable para productos
public record ProductDto(Long id, String name, String description, BigDecimal price, int stock) {
} 
