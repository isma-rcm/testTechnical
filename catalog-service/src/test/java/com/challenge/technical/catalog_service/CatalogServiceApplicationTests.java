package com.challenge.technical.catalog_service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.challenge.technical.catalog_service.exception.ResourceNotFoundException;
import com.challenge.technical.catalog_service.model.Product;
import com.challenge.technical.catalog_service.repository.ProductRepository;
import com.challenge.technical.catalog_service.service.CatalogService;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CatalogServiceUnitTest {

	// 2. MOCK: La dependencia externa (R2DBC Repository)
	@Mock
	private ProductRepository repository;

	// 3. INYECTAR MOCKS: La clase que vamos a probar
	@InjectMocks
	private CatalogService service;

	@Test
	void findById_NotFound_ThrowsResourceNotFoundException() {
		// GIVEN
		Mockito.when(repository.findById(99L)).thenReturn(Mono.empty());

		// WHEN
		Mono<Product> resultMono = service.findById(99L);

		// THEN
		StepVerifier.create(resultMono)
				// Uso de AssertJ dentro de StepVerifier
				.expectErrorSatisfies(e -> assertThat(e).isInstanceOf(ResourceNotFoundException.class))
				.verify(); // Cobertura de pruebas en lógica de negocio (manejo de errores)
	}

}
