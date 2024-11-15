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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class QueueApiTests {

    @Autowired
    private QueueApi queueApi;

    @Test
    void getQueues_shouldReturnQueueData()  {
        // Given
        Integer tournamentId = 160189; // Consider moving this to test properties

        // When
        QueueDataResponse response = queueApi.getQueues(tournamentId);

        // Then
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.arenaQueues(), "Arena queues map should not be null");
        assertThat(response.arenaQueues()).isNotEmpty();

        // Verify structure of first entry
        String firstArenaId = response.arenaQueues().keySet().iterator().next();
        assertThat(response.arenaQueues().get(firstArenaId))
                .isNotNull()
                .isNotEmpty();

        // Verify first queue entry has required fields populated
        QueueEntry firstEntry = response.arenaQueues().get(firstArenaId).get(0);
        assertThat(firstEntry.queueId()).isPositive();
        assertThat(firstEntry.tournamentId()).isEqualTo(tournamentId.longValue());
        assertThat(firstEntry.arenaId()).isPositive();
        assertThat(firstEntry.playerId()).isPositive();
    }

    @Test
    void getQueues_withInvalidTournament_shouldReturn404() {
        // Given
        Integer invalidTournamentId = -1;

        // When/Then
        assertThatThrownBy(() -> queueApi.getQueues(invalidTournamentId))
                .isInstanceOf(HttpClientErrorException.NotFound.class)
                .hasMessageContaining("404 Not Found");
    }
}