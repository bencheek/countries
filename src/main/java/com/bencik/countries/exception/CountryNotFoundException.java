package com.bencik.countries.exception;

public class CountryNotFoundException extends RuntimeException {
    public CountryNotFoundException(String country) {
        super(String.format("Country %s does not exist", country));
    }
}
