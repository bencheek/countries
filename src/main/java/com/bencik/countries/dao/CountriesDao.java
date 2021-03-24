package com.bencik.countries.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Slf4j
@Repository
public class CountriesDao {

    @Value("${json.file.url}")
    private String JSON_FILE_URL;

    @Value("${json.path.countryCode}")
    private String JSON_PATH_COUNTRY_CODE;

    @Value("${json.path.countryBorders}")
    private String JSON_PATH_COUNTRY_BORDERS;

    private final ResourceLoader resourceLoader;
    private final Map<String, Set<String>> borders = new HashMap<>();

    public CountriesDao(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @SneakyThrows
    @PostConstruct
    public void loadDb() {
        Resource resource = resourceLoader.getResource(JSON_FILE_URL);
        ObjectMapper mapper = new ObjectMapper();

        @Cleanup InputStream inputStream = resource.getInputStream();
        JsonNode countriesDb = mapper.readTree(inputStream);
        Iterator<JsonNode> countriesIterator = countriesDb.iterator();
        while (countriesIterator.hasNext()) {
            JsonNode countryNode = countriesIterator.next();
            Set<String> bordersSet = new HashSet<>();
            countryNode.path(JSON_PATH_COUNTRY_BORDERS).elements().forEachRemaining(border -> bordersSet.add(border.asText()));
            borders.put(countryNode.path(JSON_PATH_COUNTRY_CODE).asText(), bordersSet);
        }

        log.debug("Database of countries successfully loaded");

    }

    public boolean countryExists(String countryCode) {
        return borders.containsKey(countryCode);
    }

    public Set<String> getCountryBorders(String countryCode) {
        return borders.get(countryCode);
    }

    public Set<String> getCountryCodes() {
        return new HashSet<>(borders.keySet());
    }

}
