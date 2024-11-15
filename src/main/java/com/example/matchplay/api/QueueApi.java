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


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class QueueApi {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public QueueApi(RestClient restClient) {
        this.restClient = restClient;
    }

    public QueueDataResponse getQueues(Integer tournamentId) {
        logger.debug("Fetching queues for tournament {}", tournamentId);

        try {
            String responseBody = this.restClient.get()
                    .uri("/tournaments/" + tournamentId + "/queues")
                    .retrieve()
                    .body(String.class);

            return objectMapper.readValue(responseBody, QueueDataResponse.class);
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse queue data for tournament {}", tournamentId, e);
            throw new RuntimeException("Failed to parse queue response", e);
        } catch (Exception e) {
            logger.error("Failed to process queue data for tournament {}", tournamentId, e);
            throw e;
        }
    }


}