package com.fhv.weatherapp.service.weatherupdater.web;

public class ForecastRequestException extends Exception {
    public ForecastRequestException(Throwable cause) {
        super(cause);
    }

    public ForecastRequestException(String message) {
        super(message);
    }
}
