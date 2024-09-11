package com.example.matchplay.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class StandingsApi {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RestClient restClient;

    public StandingsApi(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<Standing> getStandings(Integer tourneyId) {
        ResponseEntity<String> responseEntity = this.restClient.get()
                .uri("/tournaments/" + tourneyId + "/standings")
                .retrieve()
                .toEntity(String.class);
        List<Standing> standings = parseJsonToStandings(responseEntity.getBody());
        return standings;
    }

    private List<Standing> parseJsonToStandings(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Standing> standings = objectMapper.readValue(jsonString, new TypeReference<List<Standing>>() {});
//            for (Standing standing : standings) {
//                System.out.println(standing);
//            }
            return standings;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>(); // Return an empty list if parsing fails
    }


}
