package com.challenge.technical.inventory_service.service;

import com.challenge.technical.inventory_service.dto.InventoryDetail;

import reactor.core.publisher.Mono;
// Servicio para manejar la lógica de inventario
public interface InventoryService {
    public Mono<InventoryDetail> getInventoryDetails(Long productId);
}
