package com.sap.co2calculator.service;

import com.sap.co2calculator.model.Co2Result;
import com.sap.co2calculator.model.TransportationMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EmissionServiceTest {

    private EmissionService emissionService;

    @BeforeEach
    void setUp(){
        emissionService = new EmissionService();
    }


    @Test
    void shouldCalculateEmission_Success(){

        //Arrange
        double distanceKm = 123.45;
        TransportationMethod method = TransportationMethod.DIESEL_CAR_SMALL;

        //Act
        Co2Result result = emissionService.calculateEmission(distanceKm, method);

        //Assert
        assertEquals(distanceKm,result.distanceKm());
        assertEquals(new BigDecimal("17.5"),result.co2Kilograms());
    }

    @Test
    void shouldRoundEmissionToOneDecimalPlace(){

        //Arrange
        double distanceKm = 123.456789;
        TransportationMethod method = TransportationMethod.PETROL_CAR_SMALL;

        //Act
        Co2Result result = emissionService.calculateEmission(distanceKm, method);

        //Assert
        assertEquals(distanceKm,result.distanceKm());
        assertEquals(new BigDecimal("19.0"),result.co2Kilograms());
    }

}