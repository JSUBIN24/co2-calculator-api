package com.sap.co2calculator.model;

import lombok.Getter;

import java.util.Map;

@Getter
public enum TransportationMethod {

    DIESEL_CAR_SMALL("diesel-car-small", 142),
    PETROL_CAR_SMALL("petrol-car-small", 154),
    PLUGIN_HYBRID_CAR_SMALL("plugin-hybrid-car-small", 73),
    ELECTRIC_CAR_SMALL("electric-car-small", 50),

    DIESEL_CAR_MEDIUM("diesel-car-medium", 171),
    PETROL_CAR_MEDIUM("petrol-car-medium", 192),
    PLUGIN_HYBRID_CAR_MEDIUM("plugin-hybrid-car-medium", 110),
    ELECTRIC_CAR_MEDIUM("electric-car-medium", 58),

    DIESEL_CAR_LARGE("diesel-car-large", 209),
    PETROL_CAR_LARGE("petrol-car-large", 282),
    PLUGIN_HYBRID_CAR_LARGE("plugin-hybrid-car-large", 126),
    ELECTRIC_CAR_LARGE("electric-car-large", 73),

    BUS_DEFAULT("bus-default", 27),
    TRAIN_DEFAULT("train-default", 6);

    private final String code;
    private final int gramsPerKm;


    TransportationMethod(String code, int gramsPerKm) {
        this.code = code;
        this.gramsPerKm = gramsPerKm;
    }


    private static final Map<String, TransportationMethod> CODE_MAP =
            Map.ofEntries(
                    Map.entry("diesel-car-small", DIESEL_CAR_SMALL),
                    Map.entry("petrol-car-small", PETROL_CAR_SMALL),
                    Map.entry("plugin-hybrid-car-small", PLUGIN_HYBRID_CAR_SMALL),
                    Map.entry("electric-car-small", ELECTRIC_CAR_SMALL),
                    Map.entry("diesel-car-medium", DIESEL_CAR_MEDIUM),
                    Map.entry("petrol-car-medium", PETROL_CAR_MEDIUM),
                    Map.entry("plugin-hybrid-car-medium", PLUGIN_HYBRID_CAR_MEDIUM),
                    Map.entry("electric-car-medium", ELECTRIC_CAR_MEDIUM),
                    Map.entry("diesel-car-large", DIESEL_CAR_LARGE),
                    Map.entry("petrol-car-large", PETROL_CAR_LARGE),
                    Map.entry("plugin-hybrid-car-large", PLUGIN_HYBRID_CAR_LARGE),
                    Map.entry("electric-car-large", ELECTRIC_CAR_LARGE),
                    Map.entry("bus-default", BUS_DEFAULT),
                    Map.entry("train-default", TRAIN_DEFAULT)
            );


    public static TransportationMethod fromString(String code) {
        TransportationMethod method = CODE_MAP.get(code.toLowerCase());
        if (method == null) {
            throw new IllegalArgumentException("Unsupported transportation method: " + code);
        }
        return method;
    }


    //Alternative way to get enum from string
/*    public static TransportationMethod fromString(String code) {
        return Arrays.stream(values())
                .filter(tm -> tm.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported transportation method: " + code));
    }*/

}
