package com.sap.co2calculator.service;

import com.sap.co2calculator.model.Co2Result;
import com.sap.co2calculator.model.TransportationMethod;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class EmissionService {

    public Co2Result calculateEmission(double distanceKm, TransportationMethod method) {

        if (distanceKm <= 0) {
            throw new IllegalArgumentException("Distance must be non negative");
        }

        if (method == null) {
            throw new IllegalArgumentException("Transportation method must be specified");
        }
        int gramsPerKm = method.getGramsPerKm();
        double gramsTotal = gramsPerKm * distanceKm;
        BigDecimal kilograms = BigDecimal.valueOf(gramsTotal / 1000.0)
                .setScale(1, RoundingMode.HALF_UP);
        return new Co2Result(distanceKm, kilograms);
    }
}
