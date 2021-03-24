package com.bencik.countries;

import com.bencik.countries.exception.CountryNotFoundException;
import com.bencik.countries.exception.RouteNotFoundException;
import com.bencik.countries.model.Route;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests the REST API endpoint
 */
@ContextConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestTemplateTest {

    public static final String CZECH_CCA3 = "CZE";
    public static final String ITALY_CCA3 = "ITA";
    public static final String USA_CCA3 = "USA";
    public static final String AUSTRIA_CCA3 = "AUT";
    public static final String NONEXISTENT_CCA3 = "CZA";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int randomServerPort;

    @Test
    public void testRouteFromCzechToItaly() {
        testValidRoute(CZECH_CCA3, ITALY_CCA3, Arrays.asList(CZECH_CCA3, AUSTRIA_CCA3, ITALY_CCA3));
    }

    @Test
    public void testRouteFromCzechToCzech() {
        testValidRoute(CZECH_CCA3, CZECH_CCA3, Arrays.asList(CZECH_CCA3));
    }

    @Test
    public void testNonExistentRoute() {
        ResponseEntity<String> response = sendInvalidRouteRequest(CZECH_CCA3, USA_CCA3);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertEquals(response.getBody(), new RouteNotFoundException(CZECH_CCA3, USA_CCA3).getMessage());
    }

    @Test
    public void testNonExistentOrigin() {
        ResponseEntity<String> response = sendInvalidRouteRequest(NONEXISTENT_CCA3, ITALY_CCA3);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertEquals(response.getBody(), new CountryNotFoundException(NONEXISTENT_CCA3).getMessage());
    }

    @Test
    public void testNonExistentTarget() {
        ResponseEntity<String> response = sendInvalidRouteRequest(ITALY_CCA3, NONEXISTENT_CCA3);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertEquals(response.getBody(), new CountryNotFoundException(NONEXISTENT_CCA3).getMessage());
    }


    private void testValidRoute(String origin, String destination, List<String> expectedRoute) {
        ResponseEntity<Route> response = sendValidRouteRequest(origin, destination);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getRoute(), expectedRoute);
    }

    private ResponseEntity<Route> sendValidRouteRequest(String origin, String destination) {
        ResponseEntity<Route> response = testRestTemplate.getForEntity(getEndpointUrl() + String.format("/%s/%s", origin, destination), Route.class);
        assertNotNull(response);
        assertNotNull(response.getBody());
        return response;
    }

    private ResponseEntity<String> sendInvalidRouteRequest(String origin, String destination) {
        ResponseEntity<String> response = testRestTemplate.getForEntity(getEndpointUrl() + String.format("/%s/%s", origin, destination), String.class);
        assertNotNull(response);
        assertNotNull(response.getBody());
        return response;
    }

    private String getEndpointUrl() {
        return String.format("http://localhost:%d/routing", randomServerPort);
    }
}
