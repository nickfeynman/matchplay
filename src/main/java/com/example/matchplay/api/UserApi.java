package com.example.matchplay.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.Map;

public class UserApi {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RestClient restClient;

    public UserApi(RestClient restClient) {
        this.restClient = restClient;
    }

    @Cacheable(value = "userNameCache", key = "#userId")
    public String getUserName(Integer userId) {
        logger.info("Cache miss for userId: {}", userId);
        ResponseEntity<String> responseEntity = this.restClient.get()
                .uri("/users/" + userId)
                .retrieve()
                .toEntity(String.class);
        String userName = parseJsonToUserName(responseEntity.getBody());
        logger.info("Retrieved UserName: {} for UserId: {}", userName, userId);
        return userName;
    }

    public static String parseJsonToUserName(String json) {
        ObjectMapper objectMapper = new ObjectMapper();

        // Parse the JSON string to a Map
        Map<String, Object> jsonMap = null;
        try {
            jsonMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // Get the "user" object from the map
        Map<String, Object> userMap = (Map<String, Object>) jsonMap.get("user");

        // Extract the name from the user object
        return (String) userMap.get("name");
    }

}
