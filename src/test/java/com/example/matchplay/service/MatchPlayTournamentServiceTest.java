package com.example.matchplay.service;

import com.example.matchplay.api.RoundDisplay;
import com.example.matchplay.configuration.MatchPlayConfigurationProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"matchplay.tournament-id=149146"})
public class MatchPlayTournamentServiceTest {


    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private MatchPlayConfigurationProperties matchPlayConfigurationProperties;

    @Test
    public void testTournamentService() {
        assertThat(tournamentService).isNotNull();
        assertThat(matchPlayConfigurationProperties.getTournamentId()).isEqualTo(149146);
        tournamentService.setActivePinId(2);
        RoundDisplay roundDisplay = tournamentService.getLatestRoundForActivePinId();

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
}