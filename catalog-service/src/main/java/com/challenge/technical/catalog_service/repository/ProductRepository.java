package com.challenge.technical.catalog_service.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.challenge.technical.catalog_service.model.Product;

import reactor.core.publisher.Flux;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
    Flux<Product> findByNameContainingIgnoreCase(String name);
    Flux<Product> findByStockLessThan(int stock);
    
}
