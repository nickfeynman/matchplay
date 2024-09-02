package com.example.matchplay.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GameNameServiceTest {

    @Test
    public void testGetGameName() {

        GameNameService gameNameService = new GameNameService();
        // Perform assertions
        assertThat(gameNameService.getGameName(1))
                .isEqualTo("Star Trek: The Next Generation");

        assertThat(gameNameService.getGameName(7))
                .isEqualTo("Spectrum");
    }
}
