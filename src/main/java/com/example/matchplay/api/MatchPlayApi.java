package com.example.matchplay.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

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

    public static final RoundDisplay DEFAULT_ROUND_DISPLAY = new RoundDisplay(
            "N/A", "N/A", "N/A", List.of("N/A"), List.of("N/A")
    );
    private final Map<GameKey, String> gameNameCache = new HashMap<>();
    private final RestClient restClient;
    private Map<Integer, String> userNameCache = new HashMap<>();

    public MatchPlayApi(RestClient.Builder restClientBuilder) {

        this.restClient = restClientBuilder.baseUrl("https://app.matchplay.events/api/")
                .defaultHeaders(h -> {
                    h.setBearerAuth("310|zLxG2hIAN6mIEQDilxRAiyVy2mzj3Do3Q7nBSemNa5893106");
                    h.setContentType(MediaType.APPLICATION_JSON);
                    h.setAccept(List.of(MediaType.APPLICATION_JSON));
                })
                .build();
    }

    public static Map<Integer, String> createGameNameMap(List<String> gameNames) {
        return IntStream.rangeClosed(1, gameNames.size())
                .boxed()
                .collect(Collectors.toMap(
                        i -> i,
                        i -> gameNames.get(i - 1),
                        (v1, v2) -> v1,
                        java.util.LinkedHashMap::new
                ));
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
                .orElse(DEFAULT_ROUND_DISPLAY);
    }


//    public List<RoundDisplay> getRoundDisplay(Integer tournyId) {
//        // Get all rounds and round details
//        List<Round> rounds = getRounds(tournyId);
//        List<RoundDetails> roundDetails = getRoundDetails(tournyId);
//
//        // Group RoundDetails by roundId
//        Map<Integer, List<RoundDetails>> roundDetailsMap = roundDetails.stream()
//                .collect(Collectors.groupingBy(RoundDetails::roundId));
//
//        // Transform rounds to RoundDisplay
//        return rounds.stream().map(round -> {
//            List<RoundDetails> details = roundDetailsMap.getOrDefault(round.roundId(), List.of());
//            if (details.isEmpty()) {
//                // If no details found, return a RoundDisplay with empty lists for userNames and points
//                return new RoundDisplay(round.name(), round.status(), "", List.of(), List.of());
//            }
//
//            // Combine information from all games in the round
//            List<String> gameNames = new ArrayList<>();
//            List<String> allUserNames = new ArrayList<>();
//            List<String> allPoints = new ArrayList<>();
//
//            for (RoundDetails detail : details) {
//                gameNames.add(getGameName(tournyId, detail.gameId()));
//                allUserNames.addAll(detail.userIds().stream().map(this::getUserName).collect(Collectors.toList()));
//                allPoints.addAll(detail.resultPoints().stream().map(Object::toString).collect(Collectors.toList()));
//            }
//
//            String combinedGameNames = String.join(", ", gameNames);
//
//            return new RoundDisplay(round.name(), round.status(), combinedGameNames, allUserNames, allPoints);
//        }).collect(Collectors.toList());
//    }

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

    public String getGameName(Integer tournyId, Integer gameId) {

        GameKey key = new GameKey(tournyId, gameId);

        // Check if the game name is already in the cache
        if (gameNameCache.containsKey(key)) {
            return gameNameCache.get(key);
        }

        ResponseEntity<String> responseEntity = this.restClient.get()
                .uri("/tournaments/" + tournyId + "/games/" + gameId)
                .retrieve()
                .toEntity(String.class);

        String gameName = parseJsonToGameName(responseEntity.getBody());

        // Store the result in the cache
        gameNameCache.put(key, gameName);

        return gameName;
    }

    public String getUserName(Integer userId) {
        if (userNameCache.containsKey(userId)) {
            return userNameCache.get(userId);
        }

        ResponseEntity<String> responseEntity = this.restClient.get()
                .uri("/users/" + userId)
                .retrieve()
                .toEntity(String.class);
        String userName = parseJsonToUserName(responseEntity.getBody());

        // Store the result in the cache
        userNameCache.put(userId, userName);

        return userName;
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
                        String gameName = getGameName(tournyId, detail.gameId());
                        List<String> userNames = detail.userIds().stream()
                                .map(this::getUserName)
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
    }
}
