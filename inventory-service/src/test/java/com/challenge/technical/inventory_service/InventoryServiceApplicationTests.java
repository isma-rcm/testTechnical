package com.challenge.technical.inventory_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.challenge.technical.inventory_service.dto.InventoryDetail;
import com.challenge.technical.inventory_service.dto.ProductDto;
import com.challenge.technical.inventory_service.service.InventoryService;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceApplicationTests {

    @Mock
    private WebClient webClient;
    
    @Mock
    private WebClient.Builder webClientBuilder;

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        // Configura el builder mock
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        
        // Crea la instancia con el URL de prueba
        inventoryService = new InventoryService(webClientBuilder, "http://localhost:8081");
    }

   private void mockWebClientSuccess(ProductDto product) {
    WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
    WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ProductDto.class)).thenReturn(Mono.just(product));
}

    @Test
    void getAvailabilityDetails_ExternalServiceSuccess_ReturnsCombinedData() {
        // GIVEN
        ProductDto mockProduct = new ProductDto(101L, "Laptop G14", "High performance laptop",
                new BigDecimal("1499.99"), 25);
        mockWebClientSuccess(mockProduct);

        // WHEN
        Mono<InventoryDetail> resultMono = inventoryService.getInventoryDetails(101L);
        
        // THEN
        StepVerifier.create(resultMono)
                .assertNext(details -> {
                    assertThat(details.productName()).isEqualTo("Laptop G14");
                    assertThat(details.quantity()).isEqualTo(25);
                    assertThat(details.status()).isEqualTo("IN_STOCK");
                    assertThat(details.warehouse()).isEqualTo("Unknown");
                })
                .verifyComplete();
    }
    
    @Test
    void getInventoryDetails_whenOutOfStock_returnsOutOfStockStatus() {
        // GIVEN
        ProductDto mockProduct = new ProductDto(102L, "Mouse", "Wireless mouse",
                new BigDecimal("29.99"), 0);
        mockWebClientSuccess(mockProduct);

        // WHEN
        Mono<InventoryDetail> resultMono = inventoryService.getInventoryDetails(102L);
        
        // THEN
        StepVerifier.create(resultMono)
                .assertNext(details -> {
                    assertThat(details.productName()).isEqualTo("Mouse");
                    assertThat(details.quantity()).isEqualTo(0);
                    assertThat(details.status()).isEqualTo("OUT_OF_STOCK");
                })
                .verifyComplete();
    }
}