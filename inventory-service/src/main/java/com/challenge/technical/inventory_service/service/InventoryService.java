package com.challenge.technical.inventory_service.service;

import java.util.Map;
import java.util.function.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.challenge.technical.inventory_service.dto.InventoryDetail;
import com.challenge.technical.inventory_service.dto.ProductDto;
import com.challenge.technical.inventory_service.exception.ExternalServiceException;

import reactor.core.publisher.Mono;

@Service
public class InventoryService {
    private final WebClient webClient;

    private final Map<Long, String> stockLocation = Map.of(10L, "Warehouse A", 20L, "Warehouse B");

    public InventoryService(WebClient.Builder webClientBuilder, @Value("${catalog.service.url}") String catalogServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(catalogServiceUrl).build();
    }

    public Mono<InventoryDetail> getInventoryDetails(Long productId) {

        // 1. WebClient: Llamada HTTP Reactiva al CatalogService
        Mono<ProductDto> productMono = webClient.get()
            .uri("/{id}", productId)
            .retrieve()
            // 2. Manejo de errores en flujos reactivos (ej. 4xx/5xx)
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse -> 
                Mono.error(new ExternalServiceException("Error del CatalogService (Status: " + clientResponse.statusCode() + ")")))
            .bodyToMono(ProductDto.class)
            // Manejo de error de conexión
            .onErrorResume(e -> Mono.error(new ExternalServiceException("Fallo de conexión con CatalogService.")));

        // 3. Map (Transformación funcional)
        return productMono.map(product -> {
            
            // 4. Predicate para la lógica de negocio
            Predicate<Integer> isAvailable = stock -> stock > 0;
            String status = isAvailable.test(product.stock()) ? "IN_STOCK" : "OUT_OF_STOCK";

            // 5. Consumer para registrar un evento (ej. logging)
            Consumer<ProductDto> logger = p -> 
                System.out.println("Inventory Check: Retrieved product " + p.id() + " with status " + status);
            logger.accept(product);

            return new InventoryDetail(
                product.id(),
                product.name(),
                product.stock(),
                status,
                stockLocation.getOrDefault(product.id(), "Unknown")
            );
        });
    }
}
