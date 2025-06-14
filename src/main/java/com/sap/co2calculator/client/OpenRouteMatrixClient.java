package com.sap.co2calculator.client;

import com.sap.co2calculator.config.AppConst;
import com.sap.co2calculator.exception.MatrixProfileException;
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

    public OpenRouteMatrixClient(@Value("${ors.token}") String token) {
        this.webClient = WebClient.builder()
                .baseUrl(AppConst.ORS_BASE_URL_MATRIC)
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
                .block();

      /*  if (response ==  null || response.distances()  == null || response.distances().isEmpty()){
            throw new IllegalStateException("Distance response from ORS API is null or empty");
        }*/

        Objects.requireNonNull(response, AppConst.ORS_API_IS_NULL_OR_EMPTY);
        Objects.requireNonNull(response.distances(), AppConst.DISTANCE_MATRIX_IS_MISSING);

        double rawKm = response.distances().getFirst().get(1) / 1000.0;

        return BigDecimal.valueOf(rawKm).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    public double fallbackGetDistanceInKm(Coordinates from, Coordinates to, Throwable throwable) {
        log.error("Fallback for matrix API: {}", throwable.getMessage());
        throw new MatrixProfileException("Matrix API failed and fallback was triggered.", throwable);
    }
}
