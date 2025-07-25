package com.sap.co2calculator.client;

import com.sap.co2calculator.exception.OrsClientException;
import com.sap.co2calculator.model.Coordinates;
import com.sap.co2calculator.model.response.MatrixResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpenRouteMatrixClientTest {

    private WebClient webClient;
    private WebClient.RequestBodyUriSpec uriSpec;
    private WebClient.RequestBodySpec requestBodySpec;
    private WebClient.ResponseSpec responseSpec;

    private OpenRouteMatrixClient matrixClient;

    @BeforeEach
    void setup() {
        webClient = mock(WebClient.class);
        uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        requestBodySpec = mock(WebClient.RequestBodySpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        WebClient.Builder builder = mock(WebClient.Builder.class);
        when(builder.baseUrl(anyString())).thenReturn(builder);
        when(builder.defaultHeader(anyString(), anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(webClient);

        matrixClient = new OpenRouteMatrixClient("5b3ce3597851110001cf62485c08d2ebd0c846c482dfd05a9835b9ed","https://api.openrouteservice.org/v2/matrix/", "driving-car");
    }

    @Test
    void shouldReturnCorrectDistanceFromResponse() {
        // Arrange
        Coordinates from = new Coordinates(13.4050, 52.5200);
        Coordinates to = new Coordinates(11.0775, 49.4491);

        MatrixResponse response = new MatrixResponse(List.of(
                List.of(0.0, 432000.0)
        ));

        // Act
        when(webClient.post()).thenReturn(uriSpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(MatrixResponse.class)).thenReturn(Mono.just(response));
        double result = matrixClient.getDistanceInKm(from, to);

        // Assert
        assertEquals(445.8, result);
    }


    @Test
    void shouldThrowMatrixProfileException_whenResponseIsNull() {
        //Arrange
        Coordinates from = new Coordinates(1.0, 2.0);
        Coordinates to = new Coordinates(3.0, 4.0);

        //Act
        when(webClient.post()).thenReturn(uriSpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(MatrixResponse.class)).thenReturn(Mono.empty());

        //Assert
        assertThrows(OrsClientException.class, () -> matrixClient.getDistanceInKm(from, to));
    }

    @Test
    void fallbackDistance_shouldThrowMatrixProfileException() {
        //Arrange
        Coordinates from = new Coordinates(1.0, 2.0);
        Coordinates to = new Coordinates(3.0, 4.0);
        Throwable cause = new OrsClientException("Simulated failure");

        //Act & Assert
        OrsClientException exception = assertThrows(OrsClientException.class, () -> {
            matrixClient.fallbackGetDistanceInKm(from, to, cause);
        });

        assertThat(exception.getMessage()).contains("Matrix API failed");
        assertThat(exception.getCause()).isEqualTo(cause);
    }

}