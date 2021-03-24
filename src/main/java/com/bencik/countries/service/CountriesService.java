package com.bencik.countries.service;

import com.bencik.countries.dao.CountriesDao;
import com.bencik.countries.exception.CountryNotFoundException;
import com.bencik.countries.exception.RouteNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CountriesService {

    private final CountriesDao countriesDao;

    public CountriesService(CountriesDao countriesDao) {
        this.countriesDao = countriesDao;
    }

    /**
     * Calculates the shortest path between origin and destination
     * using Dijkstra's algorithm
     *
     * @param origin origin country code
     * @param destination destination country code
     * @return the route
     * @throws RouteNotFoundException if route does not exist
     * @throws CountryNotFoundException if country code does not exist
     */
    @Cacheable("routings")
    public List<String> findRoute(String origin, String destination) {

        if (!countriesDao.countryExists(origin)) {
            throw new CountryNotFoundException(origin);
        }

        if (!countriesDao.countryExists(destination)) {
            throw new CountryNotFoundException(destination);
        }

        Map<String, Integer> distanceFromOriginToCountry = new HashMap<>();
        Map<String, String> previousCountry = new HashMap<>();
        Set<String> unvisitedCountries = countriesDao.getCountryCodes();

        distanceFromOriginToCountry.put(origin, 0);

        while (!unvisitedCountries.isEmpty()) {

            String closestToOrigin = distanceFromOriginToCountry.entrySet().stream()
                    .filter(entry -> unvisitedCountries.contains(entry.getKey()))
                    .min(Comparator.comparing(Map.Entry::getValue)).orElseThrow(() -> new RouteNotFoundException(origin, destination)).getKey();


            unvisitedCountries.remove(closestToOrigin);

            if (closestToOrigin.equals(destination)) {

                List<String> route = new ArrayList<>();
                String country = destination;
                do {
                    route.add(0, country);
                    country = previousCountry.get(country);
                } while (country != null);

                return route;
            }

            for (String neighbor : countriesDao.getCountryBorders(closestToOrigin)) {
                Integer neighborDistance = distanceFromOriginToCountry.get(closestToOrigin) + 1;
                if (!distanceFromOriginToCountry.containsKey(neighbor) || neighborDistance < distanceFromOriginToCountry.get(neighbor)) {
                    distanceFromOriginToCountry.put(neighbor, neighborDistance);
                    previousCountry.put(neighbor, closestToOrigin);
                }
            }
        }

        throw new RouteNotFoundException(origin, destination);
    }
}
