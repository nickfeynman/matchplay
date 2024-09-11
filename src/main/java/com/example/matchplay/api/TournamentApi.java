package com.example.matchplay.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.client.RestClient;

public class TournamentApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RestClient restClient;

    private final ObjectMapper objectMapper;

    public TournamentApi(RestClient restClient) {
        this.restClient = restClient;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Cacheable(value = "playerIdUserIdCache", key = "#playerId")
    public int getUserIdFromPlayerId(int playerId, Integer tournamentId) {
        logger.info("Retrieving userId for playerid {}", playerId);
        Tournament tournament = getTournament(tournamentId);
        Tournament.Player player = getPlayerById(tournament, playerId);
        return player.claimedBy();
    }

    public Tournament.Player getPlayerById(Tournament tournament, long playerId) {
        if (tournament == null || tournament.data() == null || tournament.data().players() == null) {
            logger.warn("Tournament or player data is null");
            return null;
        }

        return tournament.data().players().stream()
                .filter(player -> player.playerId() == playerId)
                .findFirst()
                .orElse(null);
    }

    public Tournament getTournament(Integer tournamentId) {
        String response = this.restClient.get()
                .uri("/tournaments/" + tournamentId + "?includePlayers=1")
                .retrieve()
                .body(String.class);

        return parseJsonToTournament(response);
    }

    private Tournament parseJsonToTournament(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, Tournament.class);
        } catch (Exception e) {
            logger.error("Error parsing JSON to Tournament", e);
            return null;
        }
    }

}
