import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.web.reactive.function.client.WebClient;

import com.challenge.technical.inventory_service.dto.ProductDto;
import com.challenge.technical.inventory_service.service.InventoryService;

import reactor.core.publisher.*;
import reactor.test.StepVerifier;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceApplicationTests {
    
    // Mocks de la cadena de WebClient para simular la llamada HTTP
    @Mock private WebClient webClient;
    @Mock private WebClient.ResponseSpec responseSpec;
    // ... otros mocks de WebClient ...

    @InjectMocks private InventoryService inventoryService;

    // ... Método auxiliar para configurar el mock exitoso ...
    private void mockWebClientSuccess(ProductDto product) {
        // Encadenamiento de Mocks para simular: webClient.get().uri().retrieve().bodyToMono()
        Mockito.when(webClient.get()).thenReturn(/* mock uri spec */);
        Mockito.when(/* retrieve() */).thenReturn(responseSpec);
        Mockito.when(responseSpec.bodyToMono(ProductDto.class)).thenReturn(Mono.just(product));
    }

    @Test
    void getAvailabilityDetails_ExternalServiceSuccess_ReturnsCombinedData() {
        // GIVEN
        ProductDto mockProduct = new ProductDto(101L, "Laptop G14", "...", BigDecimal.valueOf(1500.00), 30);
        mockWebClientSuccess(mockProduct);
        
        // WHEN
        Mono<ProductAvailability> resultMono = inventoryService.getAvailabilityDetails(101L);

        // THEN
        StepVerifier.create(resultMono)
            .assertNext(details -> {
                // Assertions para el dato enriquecido
                assertThat(details.productName()).isEqualTo("Laptop G14");
            })
            .verifyComplete();
    }
}