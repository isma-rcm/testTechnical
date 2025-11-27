package com.challenge.technical.catalog_service.service;

import com.challenge.technical.catalog_service.model.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CatalogService {
    // --- CRUD OPERATIONS ---
    public Mono<Product> findById(Long id);

    public Mono<Product> create(Product product);

    public Mono<Product> update(Long id, Product product);

    public Flux<Product> findAll();
    
    public Flux<Product> findLowStockProducts(int minStock);

    public Mono<Void> deleteById(Long id);

}
