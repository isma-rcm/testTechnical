package com.challenge.technical.catalog_service;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.challenge.technical.catalog_service.controller.CatalogController;
import com.challenge.technical.catalog_service.exception.ResourceNotFoundException;
import com.challenge.technical.catalog_service.model.Product;
import com.challenge.technical.catalog_service.repository.ProductRepository;
import com.challenge.technical.catalog_service.service.impl.CatalogServiceImpl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CatalogServiceUnitTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private CatalogServiceImpl service;

    @InjectMocks
    private CatalogServiceImpl serviceUnderTest;

    @InjectMocks
    private CatalogController controller;

    private Product createTestProduct(Long id) {
        return new Product(id, "TestName", "Desc", BigDecimal.TEN, 10);
    }

    // TESTS DEL SERVICIO

    @Test
    void findById_whenProductExists_returnsProduct() {
        Product expected = createTestProduct(1L);
        when(repository.findById(1L)).thenReturn(Mono.just(expected));
        Mono<Product> result = serviceUnderTest.findById(1L);
        StepVerifier.create(result)
                .expectNext(expected)
                .verifyComplete();
        verify(repository).findById(1L);
    }

    @Test
    void findById_NotFound_ThrowsResourceNotFoundException() {
        when(repository.findById(99L)).thenReturn(Mono.empty());
        Mono<Product> resultMono = serviceUnderTest.findById(99L);
        StepVerifier.create(resultMono)
                .expectErrorSatisfies(e -> assertThat(e).isInstanceOf(ResourceNotFoundException.class))
                .verify();
    }

    @Test
    void findAll_whenProductsExist_returnsAllProducts() {
        Product p1 = createTestProduct(1L);
        Product p2 = createTestProduct(2L);
        when(repository.findAll()).thenReturn(Flux.just(p1, p2));
        Flux<Product> result = serviceUnderTest.findAll();
        StepVerifier.create(result)
                .expectNext(p1)
                .expectNext(p2)
                .verifyComplete();
    }

    @Test
    void create_whenValidProduct_returnsSavedProduct() {
        Product newProduct = new Product(null, "Laptop", "Gaming", BigDecimal.valueOf(1500), 5);
        Product savedProduct = new Product(1L, "Laptop", "Gaming", BigDecimal.valueOf(1500), 5);
        when(repository.save(any(Product.class))).thenReturn(Mono.just(savedProduct));
 
        Mono<Product> result = serviceUnderTest.create(newProduct);
        StepVerifier.create(result)
                .expectNext(savedProduct)
                .verifyComplete();
        verify(repository).save(any(Product.class));
    }

    @Test
    void update_whenProductExists_returnsUpdatedProduct() {
        Long id = 1L;
        Product existingProduct = createTestProduct(id);
        Product updatedProduct = new Product(id, "Updated", "New Desc", BigDecimal.valueOf(20), 20);
        when(repository.findById(id)).thenReturn(Mono.just(existingProduct));
        when(repository.save(any(Product.class))).thenReturn(Mono.just(updatedProduct));
        Mono<Product> result = serviceUnderTest.update(id, updatedProduct);
        StepVerifier.create(result)
                .expectNext(updatedProduct)
                .verifyComplete();
        verify(repository).findById(id);
        verify(repository).save(any(Product.class));
    }

    @Test
    void update_whenProductNotFound_throwsException() {
        Long id = 99L;
        Product product = createTestProduct(id);
        when(repository.findById(id)).thenReturn(Mono.empty());
        Mono<Product> result = serviceUnderTest.update(id, product);
        StepVerifier.create(result)
                .expectErrorSatisfies(e -> assertThat(e).isInstanceOf(ResourceNotFoundException.class))
                .verify();
    }


    @Test
    void deleteById_whenProductNotFound_throwsException() {
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Mono.empty());
        Mono<Void> result = serviceUnderTest.deleteById(id);
        StepVerifier.create(result)
                .expectErrorSatisfies(e -> assertThat(e).isInstanceOf(ResourceNotFoundException.class))
                .verify();
    }

    // TESTS DEL CONTROLADOR
    @Test
    void getProductById_whenProductExists_returnsProduct() {
        Product expected = createTestProduct(1L);
        when(service.findById(1L)).thenReturn(Mono.just(expected));
        Mono<Product> result = controller.getProductById(1L);
        StepVerifier.create(result)
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void getAllProducts_whenProductsExist_returnsAllProducts() {
        Product p1 = createTestProduct(1L);
        Product p2 = createTestProduct(2L);
        Product p3 = createTestProduct(3L);
        when(service.findAll()).thenReturn(Flux.just(p1, p2, p3));
        Flux<Product> result = controller.getAllProducts();
        StepVerifier.create(result)
                .expectNext(p1)
                .expectNext(p2)
                .expectNext(p3)
                .verifyComplete();
    }

    @Test
    void getAllProducts_whenNoProducts_returnsEmptyFlux() {
        when(service.findAll()).thenReturn(Flux.empty());
        Flux<Product> result = controller.getAllProducts();
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void createProduct_whenValidProduct_returnsCreatedProduct() {
        Product newProduct = createTestProduct(null);
        Product savedProduct = createTestProduct(1L);
        when(service.create(any(Product.class))).thenReturn(Mono.just(savedProduct));
        Mono<Product> result = controller.createProduct(newProduct);
        StepVerifier.create(result)
                .expectNext(savedProduct)
                .verifyComplete();
    }

    @Test
    void updateProduct_whenProductExists_returnsUpdatedProduct() {
        Long id = 1L;
        Product updated = createTestProduct(id);
        when(service.update(anyLong(), any(Product.class))).thenReturn(Mono.just(updated));
        Mono<Product> result = controller.updateProduct(id, updated);
        StepVerifier.create(result)
                .expectNext(updated)
                .verifyComplete();
    }

    @Test
    void deleteProduct_whenProductExists_deletesSuccessfully() {
        Long id = 1L;
        when(service.deleteById(id)).thenReturn(Mono.empty());
        Mono<Void> result = controller.deleteProduct(id);
        StepVerifier.create(result)
                .verifyComplete();
        verify(service, times(1)).deleteById(id);
    }
}