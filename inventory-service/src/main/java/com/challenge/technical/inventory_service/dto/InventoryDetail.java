package com.challenge.technical.inventory_service.dto;

// DTO inmutable para detalles de inventario
public record InventoryDetail(Long productId, String productName, int quantity, String status, String warehouse) {
    
}
