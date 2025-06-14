package com.sap.co2calculator.model;

import lombok.NonNull;

import java.math.BigDecimal;

public record Co2Result(double distanceKm, BigDecimal co2Kilograms) {

    @Override
    public @NonNull String toString(){
        return String.format("Your trip caused %.1fkg of CO2-equivalent.", co2Kilograms);
    }
}
