package com.sap.co2calculator.client;

import com.sap.co2calculator.model.NamedLocation;
import com.sap.co2calculator.model.response.Feature;
import com.sap.co2calculator.model.response.GeoResponse;
import com.sap.co2calculator.model.response.Geometry;
import com.sap.co2calculator.model.response.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OpenRouteGeoClientTest {

    private WebClient webClient;
    private WebClient.RequestHeadersUriSpec<?> uriSpec;
    private WebClient.RequestHeadersSpec<?> headersSpec;
    private WebClient.ResponseSpec responseSpec;

    private OpenRouteGeoClient geoClient;


    @BeforeEach
    void setup() {
        webClient = mock(WebClient.class);
        WebClient.Builder builder = mock(WebClient.Builder.class);

        uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        headersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        when(builder.baseUrl(anyString())).thenReturn(builder);
        when(builder.defaultHeader(anyString(), anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(webClient);

        geoClient = new OpenRouteGeoClient("5b3ce3597851110001cf62485c08d2ebd0c846c482dfd05a9835b9ed");
    }

    @Test
    void shouldReturnCoordinatesForValidCity() {
        // Arrange: Fake API JSON response
        var json = """
        {
          "features": [
            {
              "geometry": {
                "coordinates": [11.0775, 49.4491]
              },
              "properties": {
                "label": "Nuremberg, Bavaria, Germany"
              }
            }
          ]
        }
        """;

        GeoResponse mockResponse = new GeoResponse(List.of(
                new Feature(
                        new Geometry(new double[]{11.0775, 49.4491}),
                        new Properties("Nuremberg, Bavaria, Germany")
                )
        ));

        // Mock WebClient behavior
        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec)uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(GeoResponse.class)).thenReturn(Mono.just(mockResponse));

        // Act
        List<NamedLocation> results = geoClient.getAllCoordinates("Nuremberg");

        // Assert
        NamedLocation location = results.getFirst();
        assertThat(location.displayName()).isEqualTo("Nuremberg, BY, Germany");
        assertThat(location.coordinates().latitude()).isEqualTo(49.342418);
        assertThat(location.coordinates().longitude()).isEqualTo(11.116223);
    }
}
