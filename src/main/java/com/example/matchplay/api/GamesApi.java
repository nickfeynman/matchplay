package com.example.matchplay.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class GamesApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RestClient restClient;

    public GamesApi(RestClient restClient) {
        this.restClient = restClient;
    }

    @Cacheable(value = "gameNameCache", key = "#gameId")
    public String getGameName(Integer tournyId, Integer gameId) {
        logger.info("Cache miss for tournyId: {} and gameId: {}", tournyId, gameId);
//        GameKey key = new GameKey(tournyId, gameId);
//
//        // Check if the game name is already in the cache
//        if (gameNameCache.containsKey(key)) {
//            String gameName = gameNameCache.get(key);
//            logger.info("Using Cached GameKey {} , GameName {} ", key, gameName);
//            return gameName;
//        }

        ResponseEntity<String> responseEntity = this.restClient.get()
                .uri("/tournaments/" + tournyId + "/games/" + gameId)
                .retrieve()
                .toEntity(String.class);

        String gameName = parseJsonToGameName(responseEntity.getBody());

        logger.info("Retrieved GameName: {} for tournyId: {} and gameId: {}", gameName, tournyId, gameId);
        return gameName;

        // Store the result in the cache
//        gameNameCache.put(key, gameName);
//        logger.info("Caching GameKey {} , GameName {} ", key, gameName);
//        return gameName;
    }

    public static String parseJsonToGameName(String json) {
        ObjectMapper objectMapper = new ObjectMapper();

        // Parse the JSON string to a Map
        Map<String, Object> jsonMap = null;
        try {
            jsonMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // Get the "data" object from the map
        Map<String, Object> dataMap = (Map<String, Object>) jsonMap.get("data");

        // Get the "arena" object from the data map
        Map<String, Object> arenaMap = (Map<String, Object>) dataMap.get("arena");

        // Extract the name from the arena object
        return (String) arenaMap.get("name");
    }

}
