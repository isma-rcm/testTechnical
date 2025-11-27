package com.challenge.technical.inventory_service.service;

import com.challenge.technical.inventory_service.dto.InventoryDetail;

import reactor.core.publisher.Mono;

public interface InventoryService {
    public Mono<InventoryDetail> getInventoryDetails(Long productId);
}
