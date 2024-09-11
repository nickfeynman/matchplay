package com.example.matchplay.service;

import com.example.matchplay.api.MatchPlayApi;
import com.example.matchplay.api.RoundDisplay;
import com.example.matchplay.api.Standing;
import com.example.matchplay.api.StandingDisplay;
import com.example.matchplay.configuration.MatchPlayConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.stream.Collectors;

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
    public List<StandingDisplay> getStandings() {
        List<Standing> standings = this.matchPlayApi.getStandings(this.matchPlayConfigurationProperties.getTournamentId());
        return convertToStandingDisplay(standings);
    }

    public List<StandingDisplay> convertToStandingDisplay(List<Standing> standings) {
        return standings.stream()
                .map(this::convertStanding)
                .sorted(Comparator.comparingInt(StandingDisplay::position))  // This is the key change
                .collect(Collectors.toList());
//                .sorted(Comparator.comparing(StandingDisplay::points).reversed()
//                        .thenComparing(StandingDisplay::position))
//                .collect(Collectors.toList());
    }

    private StandingDisplay convertStanding(Standing standing) {
        int userId = this.matchPlayApi.getTournamentApi().getUserIdFromPlayerId(standing.playerId(),
                this.matchPlayConfigurationProperties.getTournamentId());
        String fullName = this.matchPlayApi.getUserApi().getUserName(userId);
        String abbreviatedName = MatchPlayApi.abbreviateLastName(fullName);
        return new StandingDisplay(
                abbreviatedName,
                standing.position(),
                standing.points()
        );
    }
    @Override
    public RoundDisplay getLatestRoundForActivePinId() {
        String name = this.gameNameService.getGameName(getActivePinId());
        if (GameNameService.GAME_NAMES.contains(name)) {
            logger.info("Getting latest round for tournament {} : pin {}", this.matchPlayConfigurationProperties.getTournamentId(), name);
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
