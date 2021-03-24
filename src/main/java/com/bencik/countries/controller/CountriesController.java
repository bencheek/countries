package com.bencik.countries.controller;

import com.bencik.countries.model.Route;
import com.bencik.countries.service.CountriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CountriesController {

    private final CountriesService countriesService;

    @Autowired
    public CountriesController(CountriesService countriesService) {
        this.countriesService = countriesService;
    }

    @GetMapping("routing/{origin}/{destination}")
    public Route getRoute(@PathVariable String origin, @PathVariable String destination) {
        return new Route(countriesService.findRoute(origin, destination));
    }

}
