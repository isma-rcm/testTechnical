package com.challenge.technical.catalog_service.service.impl;

import java.math.BigDecimal;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import com.challenge.technical.catalog_service.exception.ResourceNotFoundException;
import com.challenge.technical.catalog_service.model.Product;
import com.challenge.technical.catalog_service.repository.ProductRepository;
import com.challenge.technical.catalog_service.service.CatalogService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {
    private final ProductRepository productRepository;

    @Override
    public Mono<Product> findById(Long id) {
        return productRepository.findById(id)
                // Manejo de errores en flujos reactivos (similar a Optional.orElseThrow)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Product not found: " + id)));
    }

    @Override
    public Mono<Product> create(Product product) {
        Product newProduct = product.newProduct(product.name(), product.description(), product.price(),
                product.stock());
        return productRepository.save(newProduct);
    }

    @Override
    public Mono<Product> update(Long id, Product product) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Product not found: " + id)))
                // Usa el ID existente para crear una nueva instancia inmutable
                .flatMap(existingProduct -> {
                    Product updatedProduct = new Product(
                            existingProduct.id(),
                            product.name(),
                            product.description(),
                            product.price(),
                            product.stock());
                    return productRepository.save(updatedProduct);
                });
    }

    @Override
    public Flux<Product> findAll() {
        // R2DBC devuelve directamente un Flux
        return productRepository.findAll();
    }

    // --- Aplicación de Patrones Funcionales (Supplier) ---
    @Override
    public Flux<Product> findLowStockProducts(int minStock) {

        // 1. Supplier para obtener un valor por defecto si la lista estuviera vacía
        // (ejemplo didáctico)
        Supplier<Product> defaultProductSupplier = () -> new Product(999L, "Default Item", "N/A",
                new BigDecimal("0.00"), 0);

        return productRepository.findByStockLessThan(minStock)
                // Si el flujo está vacío, usamos el Supplier para crear un valor por defecto
                // (ejemplo simple)
                .defaultIfEmpty(defaultProductSupplier.get());
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        // Primero, verificamos que el producto exista para poder manejar el error 404
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Product not found for deletion: " + id)))
                // Si existe, procedemos a eliminarlo
                .flatMap(product -> productRepository.delete(product));

    }
}
