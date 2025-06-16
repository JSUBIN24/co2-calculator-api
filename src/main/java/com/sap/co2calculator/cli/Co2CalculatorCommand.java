package com.sap.co2calculator.cli;

import com.sap.co2calculator.config.AppConst;
import com.sap.co2calculator.model.Co2Result;
import com.sap.co2calculator.model.Coordinates;
import com.sap.co2calculator.model.NamedLocation;
import com.sap.co2calculator.model.TransportationMethod;
import com.sap.co2calculator.service.DistanceService;
import com.sap.co2calculator.service.EmissionService;
import com.sap.co2calculator.service.GeoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Component
@Slf4j
@RequiredArgsConstructor
public class Co2CalculatorCommand implements CommandLineRunner {

    private final GeoService geoService;
    private final EmissionService emissionService;
    private final DistanceService distanceService;


    @Override
    public void run(String... args) {
        try {
            Map<String, String> params = parseArgs(args);

            String start = params.get(AppConst.START);
            String end = params.get(AppConst.END);
            String methodInput = params.get(AppConst.TRANSPORTATION_METHOD);

            if (validateInput(start, end, methodInput)) return;

            TransportationMethod method;
            try {
                method = TransportationMethod.fromString(methodInput);
            } catch (IllegalArgumentException ex) {
                log.error("Invalid transportation method: {}" , methodInput);
                return;
            }

            Coordinates startCord = resolveCity(start);
            Coordinates endCord = resolveCity(end);

            double distanceKm = distanceService.getDistanceInKm(startCord, endCord);
            Co2Result result = emissionService.calculateEmission(distanceKm, method);

            log.info(result.toString());

        } catch (Exception ex) {
            log.error("Error occurred during calculation: {}" ,ex.getMessage());
            throw  ex;
        }
    }


    private Coordinates resolveCity(String cityName){
        List< NamedLocation> candidates = geoService.getCandidateLocations(cityName);

        if (candidates.isEmpty()){
            log.error("No matching location found for: {}" ,cityName);
            throw new RuntimeException(String.format("No matching location found for: %s", cityName));
        }

        if(candidates.size() ==1){
            return candidates.getFirst().coordinates();
        }

        log.info(" Multiple matches found for \"{} \" cityName. Please choose one:" , cityName);
        for (int i = 0; i < candidates.size(); i++){
            NamedLocation loc = candidates.get(i);
            log.info("[{}] {} ({}, {})", i+1, loc.displayName(),loc.coordinates().latitude(),loc.coordinates().longitude());
        }
        log.info("Enter the number of your choice: ");
        Scanner scanner = new Scanner(System.in);
        int selection = scanner.nextInt();

        if (selection < 1 || selection > candidates.size()) {
            log.error(" Invalid selection.");
            throw new IllegalArgumentException("Invalid selection");
        }

        return candidates.get(selection - 1).coordinates();
    }

    private Map<String, String> parseArgs(String[] args) {
        Map<String, String> params = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.startsWith("--")) {
                String key = arg.substring(2);
                if (key.contains("=")) {
                    String[] split = key.split("=", 2);
                    params.put(split[0].toLowerCase(), stripQuotes(split[1]));
                } else if (i + 1 < args.length) {
                    params.put(key.toLowerCase(), stripQuotes(args[i + 1]));
                    i++;
                }
            }
        }

        return params;
    }

    private String stripQuotes(String value) {
        return value.replaceAll("^\"|\"$", "").trim();
    }

    private static boolean validateInput(String start, String end, String methodInput) {
        if (start == null || end == null || methodInput == null) {
            log.error("Missing required parameters: --start, --end, --transportation-method");
            return true;
        }
        return false;
    }
}
