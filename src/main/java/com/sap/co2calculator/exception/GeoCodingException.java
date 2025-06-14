package com.sap.co2calculator.exception;

public class GeoCodingException extends RuntimeException{
    public GeoCodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
