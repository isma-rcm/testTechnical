package com.challenge.technical.catalog_service.controller;

import com.challenge.technical.catalog_service.model.Product;
import com.challenge.technical.catalog_service.service.CatalogService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/catalog")
public class CatalogController {
    private final CatalogService service;   

    @GetMapping("/{id}")
    public Mono<Product> getProductById(@PathVariable Long id) {
        return service.findById(id);
    }

   @PostMapping
    public Mono<Product> createProduct(@RequestBody Product product) {
        return service.save(product);
    }
    

    
}
