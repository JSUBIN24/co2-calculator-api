package com.sap.co2calculator.service;

import com.sap.co2calculator.client.OpenRouteGeoClient;
import com.sap.co2calculator.client.OpenRouteMatrixClient;
import com.sap.co2calculator.exception.MatrixProfileException;
import com.sap.co2calculator.model.Coordinates;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DistanceServiceTest {


    @Mock
    private OpenRouteMatrixClient openRouteMatrixClient;

    @InjectMocks
    private DistanceService distanceService;

    private static Coordinates from;
    private static Coordinates to;

    @BeforeAll
    static void setUp(){
        from = new Coordinates(10.00046, 53.576158);
        to = new Coordinates(2.352222,48.858705);
    }

    @Test
    void shouldReturnDistanceInKm(){
        //Arrange
        double expectedDistanceInKm = 123.45;

        //Act
        when(distanceService.getDistanceInKm(from,to)).thenReturn(expectedDistanceInKm);
        double actualDistanceInKm = distanceService.getDistanceInKm(from,to);

        //Assert
        assertEquals(expectedDistanceInKm,actualDistanceInKm);
        assertEquals(10.00046,from.longitude());
        assertEquals(48.858705,to.latitude());
        verify(openRouteMatrixClient,times(1)).getDistanceInKm(from,to);
    }


    @Test
    void shouldReturnZeroDistance_whenFromAndToAreTheSame(){
        //Act
        double actualDistanceInKm = distanceService.getDistanceInKm(from,from);


        //Assert
        assertEquals(0.0,actualDistanceInKm);
        verify(openRouteMatrixClient,times(1)).getDistanceInKm(from,from);
    }

    @Test
    void shouldReturnZeroDistance_whenFromIsNull(){
        //Act
        double actualDistanceInKm = distanceService.getDistanceInKm(null,to);

        //Assert
        assertEquals(0.0,actualDistanceInKm);
        verify(openRouteMatrixClient,times(1)).getDistanceInKm(null,to);
    }

    @Test
    void shouldHandleCoordinatesAtBoundaryValue(){
        //Arrange
        double expectedDistanceInKm = 20000.0;
        Coordinates fromBoundary = new Coordinates(180.0,90.0);
        Coordinates toBoundary = new Coordinates(-180.0,-90.0);

        //Act
        when(distanceService.getDistanceInKm(fromBoundary,toBoundary)).thenReturn(expectedDistanceInKm);
        double actualDistanceInKm = distanceService.getDistanceInKm(fromBoundary,toBoundary);

        //Arrange
        assertEquals(expectedDistanceInKm,actualDistanceInKm);
        verify(openRouteMatrixClient,times(1)).getDistanceInKm(fromBoundary,toBoundary);


    }


    @Test
    void shouldThrowMatrixProfileException_whenMatrixClientFails(){

        //Act
        when(openRouteMatrixClient.getDistanceInKm(from,to)).thenThrow(new RuntimeException("Error occurred during matrix profile"));

        //Assert
        assertThatThrownBy(()-> distanceService.getDistanceInKm(from,to))
                .isInstanceOfAny(MatrixProfileException.class)
                .hasMessageContaining("Matrix failed for coordinated from : " + from + " to : " + to)
                .hasCauseInstanceOf(RuntimeException.class);
    }

}