package com.challenge.technical.inventory_service.dto;

import java.math.BigDecimal;

public record ProductDto(Long id, String name, String description, BigDecimal price, int stock) {
} 
