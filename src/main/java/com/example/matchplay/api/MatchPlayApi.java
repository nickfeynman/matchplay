package com.example.matchplay.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class MatchPlayApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final RoundDisplay DEFAULT_ROUND_DISPLAY = new RoundDisplay(
            "N/A", "N/A", "N/A", List.of("N/A"), List.of("N/A")
    );
    private final Map<GameKey, String> gameNameCache = new HashMap<>();
    private final RestClient restClient;
    private Map<Integer, String> userNameCache = new HashMap<>();

    private GamesApi gamesApi;

    private UserApi userApi;

    private StandingsApi standingsApi;

    public MatchPlayApi(RestClient restClient, GamesApi gamesApi, UserApi userApi, StandingsApi standingsApi) {
        this.restClient = restClient;
        this.gamesApi = gamesApi;
        this.userApi = userApi;
        this.standingsApi = standingsApi;
    }

    public UserApi getUserApi() {
        return userApi;
    }

    public List<Standing> getStandings(Integer tournyId) {
        return this.standingsApi.getStandings(tournyId);
    }
    public static List<Round> parseJsonToRounds(String json) {
        ObjectMapper objectMapper = new ObjectMapper();

        // Parse the JSON string to a Map
        Map<String, Object> jsonMap = null;
        try {
            jsonMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // Get the "data" array from the map
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) jsonMap.get("data");

        // Convert each item in the data array to a Round object
        List<Round> rounds = dataList.stream()
                .map(item -> new Round(
                        (Integer) item.get("roundId"),
                        (String) item.get("name"),
                        (String) item.get("status")
                ))
                .toList();
        List<Round> sortedRounds = new ArrayList<>(rounds);
        sortedRounds.sort(Comparator.comparing(Round::name));
        return sortedRounds;
    }


    public static List<RoundDetails> parseJsonToRoundDetails(String json) {
        ObjectMapper objectMapper = new ObjectMapper();

        // Parse the JSON string tho a Map
        Map<String, Object> jsonMap = null;
        try {
            jsonMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // Get the "data" array from the map
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) jsonMap.get("data");

        // Convert each item in the data array to a RoundDetails object
        return dataList.stream()
                .map(item -> new RoundDetails(
                        (Integer) item.get("roundId"),
                        (Integer) item.get("gameId"),
                        (List<Integer>) item.get("userIds"),
                        convertToFloatList((List<String>) item.get("resultPoints"))
                ))
                .collect(Collectors.toList());
    }

    private static List<Float> convertToFloatList(List<String> stringList) {
        return stringList.stream()
                .map(s -> {
                    if (s == null) {
                        return 0f;
                    }
                    try {
                        return Float.parseFloat(s);
                    } catch (NumberFormatException e) {
                        return 0f;
                    }
                })
                .collect(Collectors.toList());
    }

    public RoundDisplay getLatestRoundForGame(String gameName, Map<String, List<RoundDisplay>> roundDisplayMap) {

        return roundDisplayMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getKey().compareTo(e1.getKey())) // Sort rounds in descending order
                .map(entry -> entry.getValue().stream()
                        .filter(rd -> rd.gameName().equals(gameName))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .findFirst()
                .map(this::sortAndFormatRoundDisplay)
                .orElse(DEFAULT_ROUND_DISPLAY);
    }

    private RoundDisplay sortAndFormatRoundDisplay(RoundDisplay rd) {
        List<String> sortedUserNames = new ArrayList<>();
        List<String> sortedPoints = new ArrayList<>();

        // Combine userNames and points, sort them, and then separate them again
        List<Map.Entry<String, String>> combinedList = IntStream.range(0, rd.userNames().size())
                .mapToObj(i -> new AbstractMap.SimpleEntry<>(rd.userNames().get(i), rd.points().get(i)))
                .sorted((e1, e2) -> Float.compare(Float.parseFloat(e2.getValue()), Float.parseFloat(e1.getValue())))
                .collect(Collectors.toList());

        for (Map.Entry<String, String> entry : combinedList) {
            sortedUserNames.add(abbreviateLastName(entry.getKey()));
            sortedPoints.add(entry.getValue());
        }

        return new RoundDisplay(rd.name(), rd.status(), rd.gameName(), sortedUserNames, sortedPoints);
    }

    public static String abbreviateLastName(String fullName) {
        String[] nameParts = fullName.split("\\s+");
        if (nameParts.length > 1) {
            String firstName = nameParts[0];
            String lastInitial = nameParts[nameParts.length - 1].substring(0, 1) + ".";
            return firstName + " " + lastInitial;
        }
        return fullName;
    }


    public List<Round> getRounds(Integer tournyId) {
        ResponseEntity<String> responseEntity = this.restClient.get()
                .uri("/tournaments/" + tournyId + "/rounds")
                .retrieve()
                .toEntity(String.class);

        return parseJsonToRounds(responseEntity.getBody());

    }

    public List<RoundDetails> getRoundDetails(Integer tournyId) {
        ResponseEntity<String> responseEntity = this.restClient.get()
                .uri("/tournaments/" + tournyId + "/games")
                .retrieve()
                .toEntity(String.class);
        return parseJsonToRoundDetails(responseEntity.getBody());
    }

    /**
     * @param tournyId tournament ID
     * @return a Map where the key is the name of the round, e.g. "Round 1" , "Round 2" etc.
     * and the value is a list of the games in the round.
     */
    public Map<String, List<RoundDisplay>> getRoundDisplay(Integer tournyId) {
        // Get all rounds and round details
        List<Round> rounds = getRounds(tournyId);
        List<RoundDetails> roundDetails = getRoundDetails(tournyId);

        // Group RoundDetails by roundId
        Map<Integer, List<RoundDetails>> roundDetailsMap = roundDetails.stream()
                .collect(Collectors.groupingBy(RoundDetails::roundId));

        // Transform rounds to Map<String, List<RoundDisplay>>
        return rounds.stream().collect(Collectors.toMap(
                Round::name,
                round -> {
                    List<RoundDetails> details = roundDetailsMap.getOrDefault(round.roundId(), List.of());
                    if (details.isEmpty()) {
                        // If no details found, return a list with a single RoundDisplay with empty lists for userNames and points
                        return List.of(new RoundDisplay(round.name(), round.status(), "", List.of(), List.of()));
                    }

                    // Create a RoundDisplay for each game in the round
                    return details.stream().map(detail -> {
                        String gameName = this.gamesApi.getGameName(tournyId, detail.gameId());
                        List<String> userNames = detail.userIds().stream()
                                .map(this.userApi::getUserName)
                                .collect(Collectors.toList());
                        List<String> points = detail.resultPoints().stream()
                                .map(Object::toString)
                                .collect(Collectors.toList());

                        return new RoundDisplay(round.name(), round.status(), gameName, userNames, points);
                    }).collect(Collectors.toList());
                },
                (v1, v2) -> v1, // In case of duplicate keys, keep the first one
                java.util.LinkedHashMap::new // Use LinkedHashMap to maintain insertion order
        ));
    }

    private static class GameKey {
        private final Integer tournyId;
        private final Integer gameId;

        public GameKey(Integer tournyId, Integer gameId) {
            this.tournyId = tournyId;
            this.gameId = gameId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GameKey gameKey = (GameKey) o;
            return Objects.equals(tournyId, gameKey.tournyId) &&
                    Objects.equals(gameId, gameKey.gameId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tournyId, gameId);
        }

        @Override
        public String toString() {
            return "GameKey{" +
                    "tournyId=" + tournyId +
                    ", gameId=" + gameId +
                    '}';
        }
    }
}
