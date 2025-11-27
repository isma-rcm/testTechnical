package com.challenge.technical.catalog_service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.challenge.technical.catalog_service.controller.CatalogController;
import com.challenge.technical.catalog_service.exception.ResourceNotFoundException;
import com.challenge.technical.catalog_service.model.Product;
import com.challenge.technical.catalog_service.repository.ProductRepository;
import com.challenge.technical.catalog_service.service.CatalogService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceUnitTest {

	// 2. MOCK: La dependencia externa 
	@Mock
	private ProductRepository repository;

	// 3. INYECTAR MOCKS: La clase que vamos a probar
	@InjectMocks
	private CatalogService service;
	private CatalogController controller;

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

	// Test para los metodos get del controlador

	@Test
	void getAllProducts_whenProductsExist_returnsAllProducts() {
		// Arrange
		Product p1 = new Product(1L, "Laptop", null, null, 0);
		Product p2 = new Product(2L, "Mouse", null, null, 0);
		Product p3 = new Product(3L, "Keyboard", null, null, 0);

		when(service.findAll())
				.thenReturn(Flux.just(p1, p2, p3));

		// Act
		Flux<Product> result = controller.getAllProducts();

		// Assert
		StepVerifier.create(result)
				.expectNext(p1)
				.expectNext(p2)
				.expectNext(p3)
				.verifyComplete();
	}

	@Test
	void getAllProducts_whenNoProducts_returnsEmptyFlux() {
		when(service.findAll())
				.thenReturn(Flux.empty());

		Flux<Product> result = controller.getAllProducts();

		StepVerifier.create(result)
				.verifyComplete();
	}

}
