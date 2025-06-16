package com.sap.co2calculator.client;

import com.sap.co2calculator.config.AppConst;
import com.sap.co2calculator.exception.OrsClientException;
import com.sap.co2calculator.model.Coordinates;
import com.sap.co2calculator.model.request.MatrixRequest;
import com.sap.co2calculator.model.response.MatrixResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class OpenRouteMatrixClient {

    private final WebClient webClient;

    public OpenRouteMatrixClient(@Value("${ors.token}") String token, @Value("${ors.base.url.matrix}") String urlPrefix ,@Value ("${ors.base.url.profile}") String urlSuffix) {
        String orsMatrixUrl = Objects.requireNonNull(urlPrefix) + Objects.requireNonNull(urlSuffix);
        this.webClient = WebClient.builder()
                .baseUrl(orsMatrixUrl)
                .defaultHeader(AppConst.AUTHORIZATION, token)
                .build();
    }

    @Retry(name= "matrix-api")
    @CircuitBreaker(name = "matrix-api", fallbackMethod = "fallbackGetDistanceInKm")
    public double getDistanceInKm(Coordinates from, Coordinates to) {
        MatrixRequest request = new MatrixRequest(
                List.of(List.of(from.longitude(), from.latitude()),
                        List.of(to.longitude(), to.latitude())),
                List.of(AppConst.DISTANCE)
        );

        MatrixResponse response = webClient.post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(MatrixResponse.class)
                .blockOptional()
                        .orElseThrow(() -> new OrsClientException(AppConst.ORS_API_IS_NULL_OR_EMPTY));

        if (response.distances()  == null || response.distances().isEmpty() || response.distances().getFirst() == null || response.distances().getFirst().size() < 2){
            throw new IllegalStateException("Invalid distance matrix structure from ORS");
        }

        Double rawKm = response.distances().getFirst().get(1);

        if (rawKm == null){
            log.error("No drivable route found between selected cities");
            throw new OrsClientException("No drivable route found between selected cities");
        }

        return BigDecimal.valueOf(rawKm/1000.0).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    public double fallbackGetDistanceInKm(Coordinates from, Coordinates to, Throwable throwable) {
        log.error("Fallback for matrix API: {}", throwable.getMessage());
        throw new OrsClientException("Matrix API failed and fallback was triggered.", throwable);
    }
}
