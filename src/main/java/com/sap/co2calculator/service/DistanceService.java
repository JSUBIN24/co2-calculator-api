package com.sap.co2calculator.service;

import com.sap.co2calculator.client.OpenRouteMatrixClient;
import com.sap.co2calculator.exception.OrsClientException;
import com.sap.co2calculator.model.Coordinates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class DistanceService {

    private final OpenRouteMatrixClient matrixClient;

    public DistanceService(OpenRouteMatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    public double getDistanceInKm(Coordinates from, Coordinates to) {
        try {
            return matrixClient.getDistanceInKm(from, to);
        }
        catch (Exception e){
            log.error("Error occurred during matrix profile from {} to {}: {}", from, to, e.getMessage());
            throw new OrsClientException("Matrix failed for coordinated from : "  + from + " to : " + to,e);
        }
    }
}
