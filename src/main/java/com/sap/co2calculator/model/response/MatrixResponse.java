package com.sap.co2calculator.model.response;

import java.util.List;

public record MatrixResponse(List<List<Double>> distances) {
}
