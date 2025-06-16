package com.sap.co2calculator.client;

import com.sap.co2calculator.config.AppConst;
import com.sap.co2calculator.model.Coordinates;
import com.sap.co2calculator.model.NamedLocation;
import com.sap.co2calculator.model.response.Feature;
import com.sap.co2calculator.model.response.GeoResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class OpenRouteGeoClient {

    private final WebClient webClient;

    public OpenRouteGeoClient(@Value("${ors.token}") String token,   @Value("${ors.base.url.geocode}") String orsGeocodeUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(orsGeocodeUrl)
                .defaultHeader(AppConst.AUTHORIZATION, token)
                .build();
    }

    @Retry(name= "geo-api")
    @CircuitBreaker(name = "geo-api", fallbackMethod = "fallbackGetCoordinates")
    public List<NamedLocation> getAllCoordinates(String city) {
        GeoResponse geoResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam(AppConst.API_KEY, System.getenv(AppConst.ORS_TOKEN))
                        .queryParam(AppConst.TEXT, city)
                        .queryParam(AppConst.LAYERS, AppConst.LOCALITY)
                        .build())
                .retrieve()
                .bodyToMono(GeoResponse.class)
                .block();


        List<NamedLocation> results = new ArrayList<>();
        if (geoResponse != null && geoResponse.features() != null && !geoResponse.features().isEmpty()) {
            for (Feature feature : geoResponse.features()) {
                double[] coords = feature.geometry().coordinates();
                String name = feature.properties().label();
                results.add(new NamedLocation(name, new Coordinates(coords[0], coords[1])));
            }
        }

        return results;

    }

    public List<NamedLocation> fallbackGetCoordinates(String city, Throwable throwable) {
        log.error("Fallback method for get all coordinates for city: {}" , throwable.getMessage());
        return  List.of();
    }
}
