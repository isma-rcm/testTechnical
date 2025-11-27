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

@ExtendWith(MockitoExtension.class)  // Quita @SpringBootTest
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
        
        // Crea la instancia manualmente
        inventoryService = new InventoryService(webClientBuilder);
    }

    private void mockWebClientSuccess(ProductDto product) {
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
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
                    assertThat(details.stock()).isEqualTo(25);
                })
                .verifyComplete();
    }
}