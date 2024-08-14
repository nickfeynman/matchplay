package com.example.matchplay;

import com.example.matchplay.domain.RoundDisplay;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MatchPlayServiceTests {

    @Autowired
    private MatchPlayApi matchPlayApi;
    @Test
    void helloWorld()  {

        Integer tournyId = 149146; //149145;
        var roundDisplays = matchPlayApi.getRoundDisplay(tournyId);
        System.out.println(roundDisplays);

        String gameName = "Deadpool (Pro)";
        List<RoundDisplay> latestRoundForMachine = MatchPlayApi.getLatestRoundForMachine(gameName, roundDisplays);
        System.out.println(gameName);
        System.out.println(latestRoundForMachine);


//        List<Round> rounds = matchPlayApi.getRounds(tournyId);
//        System.out.println(rounds);
//
//        List<RoundDetails> roundDetails = matchPlayApi.getRoundDetails(tournyId);
//        System.out.println(roundDetails);



//        GetTournament200Response tournament = tournamentsApi.getTournament(149145, 149145, false, false,
//                false, false, false, false, false, false,
//                false, false);
//
//        GetRounds200Response tournamentsApiRounds = tournamentsApi.getRounds(tournyId, tournyId);
//
//        List<GetRounds200ResponseDataInner> roundsData = tournamentsApiRounds.getData();
//        List<Round> rounds = new ArrayList<>();
//        for (GetRounds200ResponseDataInner data : roundsData) {
//            Round round = new Round(data.getRoundId(), data.getName(), data.getStatus());
//            rounds.add(round);
//        }
//        rounds.sort(Comparator.comparing(Round::name));
//        System.out.println(rounds);
//
//        for (Round round : rounds) {
//            SeriesAttendance200Response gamesInTournament = tournamentsApi.getGamesInTournament(tournyId, tournyId, null, null, round.roundId(), null, null);
//            System.out.println(gamesInTournament);
//        }

        //System.out.println(rounds);


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
