package com.sap.co2calculator.model.request;

import java.util.List;
public record MatrixRequest(List<List<Double>> locations, List<String> metrics) {
}
