package com.example.matchplay.service;

import com.example.matchplay.api.BestScoresDisplay;
import com.example.matchplay.api.MatchPlayApi;
import com.example.matchplay.api.QueueApi;
import com.example.matchplay.api.QueueDataResponse;
import com.example.matchplay.api.QueueEntry;
import com.example.matchplay.api.RoundDisplay;
import com.example.matchplay.api.SinglePlayerGame;
import com.example.matchplay.api.SinglePlayerGameApi;
import com.example.matchplay.api.Standing;
import com.example.matchplay.api.StandingDisplay;
import com.example.matchplay.configuration.MatchPlayConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Map;

public class MatchPlayTournamentService implements TournamentService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final GameNameService gameNameService;

    private MatchPlayApi matchPlayApi;

    private MatchPlayConfigurationProperties matchPlayConfigurationProperties;

    private SinglePlayerGameApi singlePlayerGameApi;

    private QueueApi queueApi;

    private Integer activePinId = 1;

    public MatchPlayTournamentService(MatchPlayApi matchPlayApi, SinglePlayerGameApi singlePlayerGameApi, QueueApi queueApi, MatchPlayConfigurationProperties matchPlayConfigurationProperties) {
        this.matchPlayApi = matchPlayApi;
        this.matchPlayConfigurationProperties = matchPlayConfigurationProperties;
        this.gameNameService = new GameNameService();
        this.singlePlayerGameApi = singlePlayerGameApi;
        this.queueApi = queueApi;
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
        String name = this.gameNameService.getGameNameByPosition(getActivePinId());
        if (GameNameService.contains(name)) {
            logger.info("Getting latest round for tournament {} : pin {}", this.matchPlayConfigurationProperties.getTournamentId(), name);
            Integer tournamentId = this.matchPlayConfigurationProperties.getTournamentId();
            Map<String, List<RoundDisplay>> roundDisplays = matchPlayApi.getRoundDisplay(tournamentId);
            RoundDisplay roundDisplay = matchPlayApi.getLatestRoundForGame(name, roundDisplays);
            logger.info("Latest Round for {} : {}", name, roundDisplay);
            return roundDisplay;
        } else {
            logger.error("Game name {} not found in valid list of games {}", name, GameNameService.getAllGameNames());
            return MatchPlayApi.DEFAULT_ROUND_DISPLAY;
        }
    }

    public List<BestScoresDisplay> getBestScoresForActivePinId() {
        Integer tournamentId = this.matchPlayConfigurationProperties.getTournamentId();
        String name = this.gameNameService.getGameNameByPosition(getActivePinId());

        if (!GameNameService.contains(name)) {
            logger.error("Game name {} not found in valid list of games {}", name, GameNameService.getAllGameNames());
            return Collections.emptyList();
        }

        Integer arenaId = gameNameService.getArenaIdByPosition(getActivePinId());
        return getBestScoresForArena(tournamentId, arenaId);
    }


    @Override
    public String getCurrentPlayerNameForActivePinId() {
        // Get the game name from the active pin ID
        String gameName = this.gameNameService.getGameNameByPosition(getActivePinId());
        if (!GameNameService.contains(gameName)) {
            logger.warn("Game name {} not found in valid list of games", gameName);
            return "";
        }

        // Convert game name to arena ID
        int arenaId = gameNameService.getArenaIdByPosition(getActivePinId());

        return getCurrentPlayerName(matchPlayConfigurationProperties.getTournamentId(), arenaId);
    }

    public String getCurrentPlayerName(Integer tournamentId, Integer arenaId) {
        // Get queue data for the tournament
        QueueDataResponse queueData = queueApi.getQueues(tournamentId);

        // Get queue entries for the specific arena
        List<QueueEntry> arenaEntries = queueData.arenaQueues().get(String.valueOf(arenaId));

        // If no entries found, return empty string
        if (arenaEntries == null || arenaEntries.isEmpty()) {
            logger.debug("No queue entries found for arena {}", arenaId);
            return "";
        }

        // Find entry with lowest index
        QueueEntry currentEntry = arenaEntries.stream()
                .min(Comparator.comparingInt(QueueEntry::index))
                .orElse(null);

        // If no valid entry found, return empty string
        if (currentEntry == null) {
            return "";
        }

        try {
            // Convert player ID to user ID
            int userId = this.matchPlayApi.getTournamentApi()
                    .getUserIdFromPlayerId((int) currentEntry.playerId(), tournamentId);

            // Get and return the user name
            var fullName = this.matchPlayApi.getUserApi().getUserName(userId);
            return MatchPlayApi.abbreviateLastName(fullName);
        } catch (Exception e) {
            logger.error("Error getting user name for player ID {}", currentEntry.playerId(), e);
            return "";
        }
    }

    public List<BestScoresDisplay> getBestScoresForArena(Integer tournamentId, Integer arenaId) {
        List<SinglePlayerGame> games = singlePlayerGameApi.getSinglePlayerGames(tournamentId, arenaId);
        String currentPlayerName = getCurrentPlayerName(tournamentId, arenaId);

        return games.stream()
                .filter(g -> g.bestGame() && !g.voided())
                .map(game -> {
                    int userId = this.matchPlayApi.getTournamentApi().getUserIdFromPlayerId(game.playerId(), tournamentId);
                    logger.info("PlayerID {}  :  UserId {}", game.playerId(), userId);


                    if (userId == 0) {
                        return null;  // Will be filtered out
                    }

                    String fullName = this.matchPlayApi.getUserApi().getUserName(userId);
                    String abbreviatedName = MatchPlayApi.abbreviateLastName(fullName);
                    String formattedScore = String.format("%,d", game.score());

                    return new BestScoresDisplay(
                            abbreviatedName,
                            formattedScore,
                            game.points() != null ? game.points() : "",
                            currentPlayerName
                    );
                })
                .filter(Objects::nonNull)
                .sorted((a, b) -> {
                    String pointsA = a.points();
                    String pointsB = b.points();

                    // If both have non-empty points, compare points
                    if (pointsA != null && !pointsA.isEmpty() &&
                            pointsB != null && !pointsB.isEmpty()) {
                        return Double.compare(Double.parseDouble(pointsB), Double.parseDouble(pointsA));
                    }

                    // Otherwise compare scores
                    return Double.compare(
                            Double.parseDouble(b.score().replace(",", "")),
                            Double.parseDouble(a.score().replace(",", ""))
                    );
                })
                .collect(Collectors.toList());
    }


    public Integer getActivePinId() {
        return activePinId;
    }

    @Override
    public void setActivePinId(Integer pinId) {
        this.activePinId = pinId;
    }
}
