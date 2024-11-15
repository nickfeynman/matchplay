package com.example.matchplay.service;

import com.example.matchplay.api.BestScoresDisplay;
import com.example.matchplay.api.RoundDisplay;
import com.example.matchplay.api.StandingDisplay;
import com.example.matchplay.configuration.MatchPlayConfigurationProperties;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"matchplay.tournament-id=160189"})
public class MatchPlayTournamentServiceTest {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private MatchPlayConfigurationProperties matchPlayConfigurationProperties;

    @Test
    public void testStandings() {
        var standings = this.tournamentService.getStandings();
        for (StandingDisplay standing : standings) {
            System.out.println(standing);
        }
    }


    @Test
    public void testTournamentService() {
        assertThat(tournamentService).isNotNull();
        assertThat(matchPlayConfigurationProperties.getTournamentId()).isEqualTo(149146);  // old gebhards tournament
        tournamentService.setActivePinId(3);
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
                        "Whirlwind",
                        List.of("Adam R.", "Peter L.", "Glenn G.", "Matt G."),  // sorted by score
                        List.of("7.0", "5.0", "3.0", "1.0")  // sorted by score
                );
    }

    @Test
    public void testGetBestScoresForArena() {
        // Using actual tournament and arena IDs from the API
        List<BestScoresDisplay> bestScoresForArena = tournamentService.getBestScoresForArena(129750, 80475);

        assertThat(bestScoresForArena)
                .isNotEmpty()
                .allSatisfy(score -> {
                    assertThat(score).isNotNull();
                    assertThat(score.name()).isNotEmpty()
                            .matches("[A-Za-z]+ [A-Z]\\.");  // Matches pattern like "John D."
                    assertThat(score.score())
                            .isNotEmpty()
                            .matches("\\d{1,3}(,\\d{3})*");  // Matches formatted numbers like "91,287,230"
                    // Points may be empty for some scores, but if present should be in format "###.##"
                    if (!score.points().isEmpty()) {
                        assertThat(score.points()).matches("\\d+\\.\\d{2}");
                    }
                });

        // Log the actual results to help refine test assertions
        for (BestScoresDisplay score : bestScoresForArena) {
            logger.info("Best score entry - Name: {}, Score: {}, Points: {}",
                    score.name(), score.score(), score.points());
        }
    }

}