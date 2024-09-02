package com.example.matchplay.service;

import com.example.matchplay.api.MatchPlayApi;
import com.example.matchplay.api.RoundDisplay;
import com.example.matchplay.api.TournamentStanding;
import com.example.matchplay.configuration.MatchPlayConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class MatchPlayTournamentService implements TournamentService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final GameNameService gameNameService;

    private MatchPlayApi matchPlayApi;

    private MatchPlayConfigurationProperties matchPlayConfigurationProperties;

    private Integer activePinId = 1;

    public MatchPlayTournamentService(MatchPlayApi matchPlayApi, MatchPlayConfigurationProperties matchPlayConfigurationProperties) {
        this.matchPlayApi = matchPlayApi;
        this.matchPlayConfigurationProperties = matchPlayConfigurationProperties;
        this.gameNameService = new GameNameService();
    }

    @Override
    public List<TournamentStanding> getStandings() {
        return null;
    }

    @Override
    public RoundDisplay getLatestRoundForActivePinId() {
        String name = this.gameNameService.getGameName(getActivePinId());
        if (GameNameService.GAME_NAMES.contains(name)) {
            Integer tournamentId = this.matchPlayConfigurationProperties.getTournamentId();
            Map<String, List<RoundDisplay>> roundDisplays = matchPlayApi.getRoundDisplay(tournamentId);
            RoundDisplay roundDisplay = matchPlayApi.getLatestRoundForGame(name, roundDisplays);
            logger.info("Latest Round for {} : {}", name, roundDisplay);
            return roundDisplay;
        } else {
            logger.error("Game name {} not found in valid list of games {}", name, GameNameService.GAME_NAMES);
            return MatchPlayApi.DEFAULT_ROUND_DISPLAY;
        }
    }

    public Integer getActivePinId() {
        return activePinId;
    }

    @Override
    public void setActivePinId(Integer pinId) {
        this.activePinId = pinId;
    }
}
