/*
 * Copyright 2024 - 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.matchplay.api;

import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.cache.jcache.config=classpath:ehcache-test.xml",
})
class SinglePlayerGameApiTests {

    @Autowired
    private SinglePlayerGameApi singlePlayerGameApi;

    @Test
    void getSinglePlayerGames_returnsGamesForArena() {
        /*
         *             154968, "Star Trek: The Next Generation",
         *             161605, "Indianapolis 500",
         *             154983, "Whirlwind",
         *             161604, "Pulp Fiction (LE)",
         *             161603, "Congo",
         *             154978, "The Shadow",
         *             161602, "The Who's Tommy Pinball Wizard"
         */

        int harvestTournamentId =  160189; //  harvest tourny
        int harvestArenaId = 154983; // congo
        List<SinglePlayerGame> games =
                singlePlayerGameApi.getSinglePlayerGames(harvestTournamentId, harvestArenaId);


        //129750; // midieval madness tourny  // arena 80475  midieval madness game
        //List<SinglePlayerGame> games = singlePlayerGameApi.getSinglePlayerGames(129750, 80475);

        // Then
        assertThat(games).isNotEmpty();

        for (SinglePlayerGame game : games) {
            System.out.println(game);
        }


        // Print sorted scores
        System.out.println("\nSorted scores by player:");
        games.stream()
                .sorted(Comparator.comparing(SinglePlayerGame::score).reversed())
                .forEach(game -> System.out.printf("Player %d: %,d points: %s%n",
                        game.playerId(),
                        game.score(),
                        game.points() != null ? game.points() : "null"));

        //  Midieval madness
//        SinglePlayerGame firstGame = games.get(0);
//        assertThat(firstGame.singlePlayerGameId()).isEqualTo(1529475);
//        assertThat(firstGame.arenaId()).isEqualTo(80475);
//        assertThat(firstGame.playerId()).isEqualTo(192051);
//        assertThat(firstGame.points()).isEqualTo("184.00");
//        assertThat(firstGame.score()).isEqualTo(46219790);
//        assertThat(firstGame.bestGame()).isTrue();
//        assertThat(firstGame.status()).isEqualTo("completed");
//
//        SinglePlayerGame secondGame = games.get(1);
//        assertThat(secondGame.singlePlayerGameId()).isEqualTo(1529491);
//        assertThat(secondGame.points()).isEqualTo("157.00");
//        assertThat(secondGame.score()).isEqualTo(5366150);
    }

}