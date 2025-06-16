package com.sap.co2calculator.cli;

import com.sap.co2calculator.exception.OrsClientException;
import com.sap.co2calculator.model.Co2Result;
import com.sap.co2calculator.model.Coordinates;
import com.sap.co2calculator.model.NamedLocation;
import com.sap.co2calculator.model.TransportationMethod;
import com.sap.co2calculator.service.DistanceService;
import com.sap.co2calculator.service.EmissionService;
import com.sap.co2calculator.service.GeoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Co2CalculatorCommandTest {

    @Mock
    private GeoService geoService;
    @Mock
    private DistanceService distanceService;
    @Mock
    private EmissionService emissionService;

    @InjectMocks
    private Co2CalculatorCommand command;

    private final NamedLocation berlin = new NamedLocation("Berlin", new Coordinates(13.4050, 52.5200));
    private final NamedLocation hamburg = new NamedLocation("Hamburg", new Coordinates(10.0000, 53.5511));

    @Test
    void shouldCalculateEmissions_whenValidArgsProvided() {
        //Arrange
        String[] args = {
                "--start", "Berlin",
                "--end", "Hamburg",
                "--transportation-method", "diesel-car-medium"
        };

        //Act
        when(geoService.getCandidateLocations("Berlin")).thenReturn(List.of(berlin));
        when(geoService.getCandidateLocations("Hamburg")).thenReturn(List.of(hamburg));
        when(distanceService.getDistanceInKm(any(), any())).thenReturn(289.5);
        when(emissionService.calculateEmission(289.5, TransportationMethod.DIESEL_CAR_MEDIUM)).thenReturn(new Co2Result(289.5, new BigDecimal("49.2")));

        //Assert
        assertDoesNotThrow(() -> command.run(args));

        verify(distanceService).getDistanceInKm(berlin.coordinates(), hamburg.coordinates());
        verify(emissionService,times(1)).calculateEmission(289.5, TransportationMethod.DIESEL_CAR_MEDIUM);
        verifyNoMoreInteractions(distanceService, emissionService);
        verify(geoService, times(2)).getCandidateLocations(any());
    }

    @Test
    void shouldCalculateEmissions_whenValidArgsProvided_Unordered() {

        //Act
        String[] args = {
                "--end", "Hamburg",
                "--transportation-method", "diesel-car-medium",
                "--start", "Berlin"
        };

        //Arrange
        when(geoService.getCandidateLocations("Berlin")).thenReturn(List.of(berlin));
        when(geoService.getCandidateLocations("Hamburg")).thenReturn(List.of(hamburg));
        when(distanceService.getDistanceInKm(any(), any())).thenReturn(289.5);
        when(emissionService.calculateEmission(289.5, TransportationMethod.DIESEL_CAR_MEDIUM)).thenReturn(new Co2Result(289.5, new BigDecimal("49.2")));

        //Assert
        assertDoesNotThrow(() -> command.run(args));
    }

    @Test
    void shouldExit_whenMissingArguments() {
        //Arrange
        String[] args = { "--start", "Berlin" };

        //Act + Assert
        command.run(args);
        verifyNoInteractions(geoService, distanceService, emissionService);
    }

    @Test
    void shouldExit_whenNoCoordinatesFound() {
        //Arrange
        String[] args = {
                "--start", "Atlantis",
                "--end", "Hamburg",
                "--transportation-method", "bus-default"
        };

        //Act
        when(geoService.getCandidateLocations("Atlantis")).thenReturn(List.of());

        //Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> command.run(args));
        assertTrue(exception.getMessage().contains("No matching location found for: Atlantis"));
    }

    @Test
    void shouldFail_givenNumericalInput(){
        //Arrange
        String start = "123";
        String[] args = {"--start", start, "--end", "Paris", "--transportation-method", "bus-default"};

        //Act + Assert
        Exception exception = assertThrows(RuntimeException.class, () -> command.run(args));
        assertEquals("No matching location found for: "+start, exception.getMessage());

    }

    @Test
    void shouldExit_whenNoRouteFound() {
        //Arrange
        String[] args = {
                "--start", "Berlin",
                "--end", "SomeIsland",
                "--transportation-method", "train-default"
        };

        //Act
        when(geoService.getCandidateLocations("Berlin")).thenReturn(List.of(berlin));
        when(geoService.getCandidateLocations("SomeIsland")).thenReturn(List.of(hamburg));
        when(distanceService.getDistanceInKm(any(), any())).thenThrow(new OrsClientException("No route"));

        //Assert
        RuntimeException ex = assertThrows(OrsClientException.class, () -> command.run(args));
        assertEquals("No route", ex.getMessage());
    }


    @Test
    void shouldPickUserSelection_givenMultipleLocationMatches() {
        // Arrange
        String[] args = {"--start", "Berlin", "--end", "Paris", "--transportation-method", "petrol-car-small"};

        Coordinates berlin1 = new Coordinates(13.0, 52.0);
        Coordinates berlin2 = new Coordinates(13.5, 52.5);
        Coordinates paris = new Coordinates(2.3522, 48.8566);

        //Act
        when(geoService.getCandidateLocations("Berlin")).thenReturn(List.of(
                new NamedLocation("Berlin A", berlin1),
                new NamedLocation("Berlin B", berlin2)
        ));
        when(geoService.getCandidateLocations("Paris")).thenReturn(List.of(new NamedLocation("Paris", paris)));

        System.setIn(new ByteArrayInputStream("2\n".getBytes()));

        when(distanceService.getDistanceInKm(berlin2, paris)).thenReturn(1050.0);
        when(emissionService.calculateEmission(1050.0, TransportationMethod.PETROL_CAR_SMALL))
                .thenReturn(new Co2Result(1050.0,new BigDecimal("10.0")));
        command.run(args);

        // Assert
        verify(geoService, times(1)).getCandidateLocations("Berlin");
        verify(geoService, times(1)).getCandidateLocations("Paris");
        verify(distanceService).getDistanceInKm(berlin2, paris);
    }

    @Test
    void shouldExit_whenUserGivenInvalidSelection() {
        // Arrange
        String[] args = {"--start", "Berlin", "--end", "Paris", "--transportation-method", "petrol-car-small"};

        Coordinates berlin1 = new Coordinates(13.0, 52.0);
        Coordinates berlin2 = new Coordinates(13.5, 52.5);
        Coordinates paris = new Coordinates(2.3522, 48.8566);

        // Act & Assert
        when(geoService.getCandidateLocations("Berlin")).thenReturn(List.of(
                new NamedLocation("Berlin A", berlin1),
                new NamedLocation("Berlin B", berlin2)
        ));
        System.setIn(new ByteArrayInputStream("5\n".getBytes()));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> command.run(args));
        assertTrue(ex.getMessage().contains("Invalid selection"));
        verifyNoInteractions(distanceService, emissionService);
    }

}