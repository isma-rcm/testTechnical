package com.challenge.technical.catalog_service.service;

import java.math.BigDecimal;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import com.challenge.technical.catalog_service.exception.ResourceNotFoundException;
import com.challenge.technical.catalog_service.model.Product;
import com.challenge.technical.catalog_service.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CatalogService  {
    private final ProductRepository productRepository;
    public Mono<Product> findById(Long id) {
        return productRepository.findById(id)
                // Manejo de errores en flujos reactivos (similar a Optional.orElseThrow)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Product not found: " + id)));
    }

    public Mono<Product> save(Product product) {
        return productRepository.save(product);
    }
    // --- Aplicación de Patrones Funcionales (Supplier) ---
    public Flux<Product> findLowStockProducts(int minStock) {
        
        // 1. Supplier para obtener un valor por defecto si la lista estuviera vacía (ejemplo didáctico)
        Supplier<Product> defaultProductSupplier = () -> new Product(999L, "Default Item", "N/A", new BigDecimal("0.00"), 0);

        return productRepository.findByStockLessThan(minStock)
                // Si el flujo está vacío, usamos el Supplier para crear un valor por defecto (ejemplo simple)
                .defaultIfEmpty(defaultProductSupplier.get());
    }
    

}
