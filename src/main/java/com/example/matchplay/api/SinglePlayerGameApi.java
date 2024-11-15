/*
 * Copyright 2024 - 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.matchplay.api;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SinglePlayerGameApi {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RestClient restClient;

    public SinglePlayerGameApi(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<SinglePlayerGame> getSinglePlayerGames(Integer tourneyId, Integer arenaId) {
        ResponseEntity<String> responseEntity = this.restClient.get()
                .uri("/tournaments/" + tourneyId + "/single-player-games?arena=" + arenaId)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .retrieve()
                .toEntity(String.class);
        return parseJsonToSinglePlayerGames(responseEntity.getBody());
    }

    private List<SinglePlayerGame> parseJsonToSinglePlayerGames(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode root = objectMapper.readTree(jsonString);
            JsonNode dataNode = root.get("data");
            return objectMapper.readValue(dataNode.toString(), new TypeReference<List<SinglePlayerGame>>() {});
        } catch (Exception e) {
            logger.error("Error parsing single player games", e);
            return new ArrayList<>();
        }
    }
}