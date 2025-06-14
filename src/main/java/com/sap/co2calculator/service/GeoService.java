package com.sap.co2calculator.service;

import com.sap.co2calculator.client.OpenRouteGeoClient;
import com.sap.co2calculator.exception.GeoCodingException;
import com.sap.co2calculator.model.NamedLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class GeoService {

    private final OpenRouteGeoClient geoClient;

    public GeoService(OpenRouteGeoClient geoClient) {
        this.geoClient = geoClient;
    }

 /*   /// This is used to choose the first place of the location
    public Optional<Coordinates> getCoordinatesForCity(String city) {
        return geoClient.getCoordinates(city);
    }*/


    public List<NamedLocation> getCandidateLocations(String city) {
        try{
            log.info("Searching for city: {}", city);
            return geoClient.getAllCoordinates(city);
        }
        catch (Exception e){
            log.error("Error occurred during geocoding: {}" ,e.getMessage());
            throw new GeoCodingException("Geocoding failed for city: " + city, e);
        }
    }
}
