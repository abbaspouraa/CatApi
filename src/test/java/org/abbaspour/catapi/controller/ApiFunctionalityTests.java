package org.abbaspour.catapi.controller;

import org.abbaspour.catapi.model.Cat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiFunctionalityTests {

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Helper method
     * @param catJson json format of the Cat object
     * @return ResponseEntity<Cat>
     */
    private ResponseEntity<Cat> postCat(String catJson) {
        if (catJson == null || catJson.equals("")) {
            catJson = "{\n" +
                    "  \"birthDate\": \"2021-04-01T17:24:06.667\",\n" +
                    "  \"favoriteFood\": \"Chicken\",\n" +
                    "  \"name\": \"Mustache\",\n" +
                    "  \"owner\": \"Amir\"\n" +
                    "}";
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(catJson, headers);
        return restTemplate.postForEntity("/api/cat/new/", entity, Cat.class);
    }


    /**
     * api/cat/single/{id}
     * Valid ID is sent in the request
     *
     * AND
     *
     * api/new/
     * Valid entity is posted.
     *
     * AND
     *
     * api/cat/delete/{id}
     * Valid entity is deleted
     */
    @Test
    public void testPostRetrieveAndDeleteObject() {

        // Add a valid cat
        ResponseEntity<Cat> response = this.postCat(null);
        assertEquals("Mustache", response.getBody().getName());

        // Make a GET request to the /api/cat/single/1 endpoint
        response = restTemplate.getForEntity("/api/cat/single/1", Cat.class);
        Cat cat = response.getBody();

        // Verify that the response contains the expected cat
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Mustache", cat.getName());
        assertEquals("Amir", cat.getOwner());

        // Delete the cat
        restTemplate.delete("/api/cat/delete/1");

        // Making sure the cat is removed
        response = restTemplate.getForEntity("/api/cat/single/1", Cat.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * api/cat/single/{id}
     * No ID is sent in the request
     */
    @Test
    public void testGetRequestWithoutId() {
        // Make a GET request to the /api/cat/single/ endpoint
        ResponseEntity<Cat> response = restTemplate.getForEntity("/api/cat/single/", Cat.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * api/cat/single/{id}
     * Invalid ID is sent in the request
     */
    @Test
    public void testRequestWithInvalidId() {
        // Make a GET request with an Id that doesn't exist
        ResponseEntity<Cat> response = restTemplate.getForEntity("/api/cat/single/111", Cat.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Make a Get request with invalid Id type.
        response = restTemplate.getForEntity("/api/cat/single/mycat", Cat.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * api/new/
     * Invalid entity is sent in the request
     * Required files are missing
     */
    @Test
    public void testPostWithInvalidBody() {
        String student = "{\n" +
                "  \"birthDate\": \"\",\n" +
                "  \"program\": \"Software Engineering\",\n" +
                "  \"name\": \"Amir\",\n" +
                "  \"School\": \"University of Calgary\"\n" +
                "}";

        ResponseEntity<Cat> response0 = this.postCat(student);
        assertEquals(HttpStatus.BAD_REQUEST, response0.getStatusCode());
        assertNull(response0.getBody().getId());

        String catJsonWithoutName = "{\n" +
                "  \"birthDate\": \"2021-04-01T17:24:06.667\",\n" +
                "  \"favoriteFood\": \"Chicken\",\n" +
                "  \"name\": \"\",\n" +
                "  \"owner\": \"Amir\"\n" +
                "}";

        String catJsonWithoutBirthDate = "{\n" +
                "  \"birthDate\": \"\",\n" +
                "  \"favoriteFood\": \"Chicken\",\n" +
                "  \"name\": \"Mustache\",\n" +
                "  \"owner\": \"Amir\"\n" +
                "}";

        ResponseEntity<Cat> response1 = this.postCat(catJsonWithoutName);
        ResponseEntity<Cat> response2 = this.postCat(catJsonWithoutName);

        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        assertNull(response1.getBody().getId());
        assertNull(response2.getBody().getId());
    }

    /**
     * api/new/
     * Invalid entity is sent in the request
     * Sending one object twice
     */
    @Test
    public void testPostSameObjectTwice() {
        // Sending same object twice
        ResponseEntity<Cat> response1 = this.postCat(null);
        ResponseEntity<Cat> response2 = this.postCat(null);

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.CONFLICT, response2.getStatusCode());
        assertNotNull(response1.getBody().getId());
        assertNull(response2.getBody().getId());
    }

    /**
     * api/new/
     * Invalid entity is sent in the request
     * Posting object with illegal characters.
     */
    @Test
    public void testPostObjectWithIllegalCharacters() {
        String illegalObject = "{\n" +
                "  \"birthDate\": \"2023-04-02T16:41:06.014Z\",\n" +
                "  \"favoriteFood\": \"<h1>Script Injection<\\h1>\",\n" +
                "  \"name\": \"<script>window.setTimeout( function() {window.location.reload()}, 30000)<\\script>\",\n" +
                "  \"owner\": \"string\"\n" +
                "}";

        // Sending same object twice
        ResponseEntity<Cat> response = this.postCat(illegalObject);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * api/cat/delete/{id}
     * Invalid Id is sent in the request
     */
    @Test
    public void testDeleteCat() {
        boolean havingError = false;

        try {
            restTemplate.delete("/api/cat/delete/123");
            restTemplate.delete("/api/cat/delete/123blabla");
        } catch (Throwable throwable) {
            // Having invalid IDs sent to the delete API should not cause the program to crash.
            havingError = true;
        }

        assertFalse(havingError);
    }

    /**
     * api/cat/list/
     * Get API list works as expected
     */
    @Test
    public void testGetAll() {
        String amirsCat = "{\n" +
                "  \"birthDate\": \"2020-07-01T17:24:06.667\",\n" +
                "  \"favoriteFood\": \"Tuna\",\n" +
                "  \"name\": \"Mustache\",\n" +
                "  \"owner\": \"Amir\"\n" +
                "}";
        // Posting two objects
        this.postCat(null);
        this.postCat(amirsCat);

        // Calling the get API
        ResponseEntity<Cat[]> response = restTemplate.getForEntity(
                "/api/cat/list/",
                Cat[].class
        );

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).length);
    }
}