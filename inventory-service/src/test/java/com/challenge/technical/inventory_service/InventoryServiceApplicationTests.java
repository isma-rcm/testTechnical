package com.challenge.technical.inventory_service;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import com.challenge.technical.inventory_service.dto.InventoryDetail;
import com.challenge.technical.inventory_service.dto.ProductDto;
import com.challenge.technical.inventory_service.service.InventoryService;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class InventoryServiceApplicationTests {

	private final WebClient webClient = Mockito.mock(WebClient.class);

	@InjectMocks
	private InventoryService service;

	@Test
	void getInventoryDetails_CatalogReturnsSuccess_ReturnsCombinedData() {
		// GIVEN
		ProductDto mockProduct = new ProductDto(10L, "Laptop", "...", new BigDecimal("100"), 5);
		// Configurar el mock para que WebClient devuelva Mono.just(mockProduct)
		mockWebClientSuccess(mockProduct);

		// WHEN
		Mono<InventoryDetail> resultMono = service.getInventoryDetails(10L);

		// THEN
		StepVerifier.create(resultMono)
				.assertNext(details -> {
					assertThat(details.productName()).isEqualTo("Laptop");
					assertThat(details.stockQuantity()).isEqualTo(5);
				})
				.verifyComplete();

		// Verificación de mocks (asegurar que la dependencia externa fue llamada)
		Mockito.verify(webClient).get();
	}

}
