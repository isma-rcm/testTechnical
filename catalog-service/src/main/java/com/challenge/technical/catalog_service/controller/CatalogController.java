package com.challenge.technical.catalog_service.controller;

import com.challenge.technical.catalog_service.model.Product;
import com.challenge.technical.catalog_service.service.CatalogService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
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
   @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un nuevo producto")
    public Mono<Product> createProduct(@RequestBody Product product) {
        return service.create(product); 
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar por ID")
    public Mono<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return service.update(id, product); 
    }
    
    @GetMapping
    @Operation(summary = "Obtener todos los productos")
    public Flux<Product> getAllProducts() {
        return service.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar producto por ID")
    public Mono<Void> deleteProduct(@PathVariable Long id) {
        return service.deleteById(id);
    }
    
}
