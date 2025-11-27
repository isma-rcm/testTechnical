package com.challenge.technical.inventory_service.dto;

public record InventoryDetail(Long productId, String productName, int quantity, String status, String warehouse) {
    
}
