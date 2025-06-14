package com.sap.co2calculator.service;

import com.sap.co2calculator.client.OpenRouteGeoClient;
import com.sap.co2calculator.exception.GeoCodingException;
import com.sap.co2calculator.model.Coordinates;
import com.sap.co2calculator.model.NamedLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeoServiceTest {

    @Mock
    private OpenRouteGeoClient geoClient;

    @InjectMocks
    private GeoService geoService;


    @Test
    void shouldReturnListOfCandidates() {
        //Arrange
        String city = "berlin";
        String start = "Hamburg, Germany";
        String end = "Paris, France";
        NamedLocation berlin = new NamedLocation(start,new Coordinates(10.00046, 53.576158));
        NamedLocation paris = new NamedLocation(end,new Coordinates(2.352222,48.858705));

        //Act
        when(geoClient.getAllCoordinates(city)).thenReturn(List.of(berlin,paris));

        //Assert
        assertEquals(2,geoService.getCandidateLocations(city).size());
        assertEquals(start,geoService.getCandidateLocations(city).getFirst().displayName());
        assertEquals(end,geoService.getCandidateLocations(city).get(1).displayName());
        verify(geoClient,times(3)).getAllCoordinates(city);
    }

    @Test
    void shouldReturnEmptyResultIfNoResult(){
        //Arrange
        String unknownCity = "UnknownCity";
        when(geoClient.getAllCoordinates(unknownCity)).thenReturn(List.of());

        //Act
        List<NamedLocation> candidates = geoService.getCandidateLocations(unknownCity);

        //Assert
        assertTrue(candidates.isEmpty());
        verify(geoClient,times(1)).getAllCoordinates(unknownCity);
    }

    @Test
    void shouldThrowGeoCodingException_whenGeoClientFails(){
        //Arrange
        String invalidCity = "InvalidCity";
        when(geoClient.getAllCoordinates(invalidCity)).thenThrow(new RuntimeException("Error occurred during geocoding"));

        //Act + Assert
        assertThatThrownBy(()-> geoService.getCandidateLocations(invalidCity))
                .isInstanceOf(GeoCodingException.class)
                        .hasMessageContaining("Geocoding failed for city: " + invalidCity)
                                .hasCauseInstanceOf(RuntimeException.class);

        verify(geoClient,times(1)).getAllCoordinates(invalidCity);
    }

}