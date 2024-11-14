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

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SinglePlayerGameApiTests {

    @Autowired
    private SinglePlayerGameApi singlePlayerGameApi;

    @Test
    void getSinglePlayerGames_returnsGamesForArena() {
        // When
        List<SinglePlayerGame> games = singlePlayerGameApi.getSinglePlayerGames(129750, 80475);

        // Then
        assertThat(games).isNotEmpty();

        SinglePlayerGame firstGame = games.get(0);
        assertThat(firstGame.singlePlayerGameId()).isEqualTo(1529475);
        assertThat(firstGame.arenaId()).isEqualTo(80475);
        assertThat(firstGame.playerId()).isEqualTo(192051);
        assertThat(firstGame.points()).isEqualTo("184.00");
        assertThat(firstGame.score()).isEqualTo(46219790);
        assertThat(firstGame.bestGame()).isTrue();
        assertThat(firstGame.status()).isEqualTo("completed");

        SinglePlayerGame secondGame = games.get(1);
        assertThat(secondGame.singlePlayerGameId()).isEqualTo(1529491);
        assertThat(secondGame.points()).isEqualTo("157.00");
        assertThat(secondGame.score()).isEqualTo(5366150);
    }

}