package com.example.matchplay.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MatchPlayApiTests {

    @Autowired
    private MatchPlayApi matchPlayApi;

    @Test
    void helloWorld() {

        Integer tournyId = 149146; // See https://app.matchplay.events/tournaments/149146/matches
        Map<String, List<RoundDisplay>> roundDisplays = matchPlayApi.getRoundDisplay(tournyId);
        System.out.println(roundDisplays);

        String gameName = "Deadpool (Pro)";
        RoundDisplay roundDisplay = matchPlayApi.getLatestRoundForGame(gameName, roundDisplays);

        System.out.println(gameName);
        System.out.println(roundDisplay);

        assertThat(roundDisplay)
                .isNotNull()
                .extracting(
                        RoundDisplay::name,
                        RoundDisplay::status,
                        RoundDisplay::gameName,
                        RoundDisplay::userNames,
                        RoundDisplay::points
                )
                .containsExactly(
                        "Round 4",
                        "completed",
                        "Deadpool (Pro)",
                        List.of("Nicholas Berry", "Hunter Hayden", "Steve Penza", "Brenton Simpson"),
                        List.of("7.0", "3.0", "1.0", "5.0")
                );

    }


//    @Test
//    void testStandings() throws ApiException {
//        TournamentsApi tournamentsApi = new TournamentsApi();
//        tournamentsApi.getApiClient().setBearerToken(bearerToken);
//        List<Object> standings = tournamentsApi.getStandings(149145, 149145);
//        for (Object standing : standings) {
//            System.out.println(standing);
//        }
//    }
}
