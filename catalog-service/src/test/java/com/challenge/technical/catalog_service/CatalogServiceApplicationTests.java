package com.challenge.technical.catalog_service;
import java.math.BigDecimal; 

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

    @Mock
    private ProductRepository repository;
    @Mock
    private CatalogService service;
    @InjectMocks
    private CatalogService serviceUnderTest; 

    @InjectMocks
    private CatalogController controller; 

    // Objeto de prueba con campos necesarios (usando BigDecimal para price)
    private Product createTestProduct(Long id) {
        return new Product(id, "TestName", "Desc", BigDecimal.TEN, 10);
    }
    
    // ----------------------------------------------------------------------
    // TESTS DEL SERVICIO (Usando serviceUnderTest)
    // ----------------------------------------------------------------------

    @Test
    void findById_NotFound_ThrowsResourceNotFoundException() {
        // GIVEN
        Mockito.when(repository.findById(99L)).thenReturn(Mono.empty());

        // WHEN
        Mono<Product> resultMono = serviceUnderTest.findById(99L); // Usamos el servicio bajo prueba

        // THEN
        StepVerifier.create(resultMono)
                .expectErrorSatisfies(e -> assertThat(e).isInstanceOf(ResourceNotFoundException.class))
                .verify();
    }

    // ----------------------------------------------------------------------
    // TESTS DEL CONTROLADOR 
    // ----------------------------------------------------------------------

    @Test
    void getAllProducts_whenProductsExist_returnsAllProducts() {
        // Arrange
        Product p1 = createTestProduct(1L);
        Product p2 = createTestProduct(2L);
        Product p3 = createTestProduct(3L);

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
        // MOCKEAR: Mockeamos la dependencia (service)
        when(service.findAll())
                .thenReturn(Flux.empty());

        // Act
        Flux<Product> result = controller.getAllProducts();

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }
}