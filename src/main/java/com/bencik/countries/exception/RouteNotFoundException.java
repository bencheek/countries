package com.bencik.countries.exception;

public class RouteNotFoundException extends RuntimeException {

    public RouteNotFoundException(String origin, String destination) {
        super(String.format("Route between %s and %s does not exist", origin, destination));
    }
}
