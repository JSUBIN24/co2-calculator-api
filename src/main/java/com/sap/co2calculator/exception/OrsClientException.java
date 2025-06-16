package com.sap.co2calculator.exception;

public class OrsClientException extends  RuntimeException{
    public OrsClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrsClientException(String message) {
        super(message);
    }
}
