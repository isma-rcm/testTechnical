package com.challenge.technical.inventory_service.service.impl;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.challenge.technical.inventory_service.dto.InventoryDetail;
import com.challenge.technical.inventory_service.dto.ProductDto;
import com.challenge.technical.inventory_service.exception.ResourceNotFoundException;
import com.challenge.technical.inventory_service.service.InventoryService;

import reactor.core.publisher.Mono;

@Service
public class InventoryServiceImpl implements InventoryService {
        private final WebClient webClient;

        private final Map<Long, String> stockLocation = Map.of(10L, "Warehouse A", 20L, "Warehouse B");

        // Constructor inyectado con WebClient.Builder y URL del servicio de catálogo
        public InventoryServiceImpl(WebClient.Builder webClientBuilder,
                        @Value("${catalog.service.url}") String catalogServiceUrl) {
                this.webClient = webClientBuilder.baseUrl(catalogServiceUrl).build();
        }

        @Override
        public Mono<InventoryDetail> getInventoryDetails(Long productId) {
                // uso de WebClient: Llamada HTTP Reactiva al CatalogService
                Mono<ProductDto> productMono = webClient.get()
                                .uri("/{id}", productId)
                                .retrieve()
                                // Manejo de errores para el flujos reactivo
                                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                                                clientResponse -> Mono.error(new ResourceNotFoundException(
                                                                "Error del CatalogService (Status: "
                                                                                + clientResponse.statusCode() + ")")))
                                .bodyToMono(ProductDto.class)
                                // Manejo de error de conexión
                                .onErrorResume(e -> Mono.error(new ResourceNotFoundException(
                                                "Fallo de conexión con CatalogService.")));

                // uso de map para transformar ProductDto a InventoryDetail
                return productMono.map(product -> {

                        // Uso de Predicate para la lógica de negocio
                        Predicate<Integer> isAvailable = stock -> stock > 0;
                        String status = isAvailable.test(product.stock()) ? "IN_STOCK" : "OUT_OF_STOCK";

                        // Uso de Consumer para registrar un evento
                        Consumer<ProductDto> logger = p -> System.out
                                        .println("Inventory Check: Retrieved product " + p.id() + " with status "
                                                        + status);
                        logger.accept(product);

                        return new InventoryDetail(
                                        product.id(),
                                        product.name(),
                                        product.stock(),
                                        status,
                                        stockLocation.getOrDefault(product.id(), "Unknown"));
                });
        }
}
