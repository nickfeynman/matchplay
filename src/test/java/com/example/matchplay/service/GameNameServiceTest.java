package com.example.matchplay.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class GameNameServiceTest {

    private GameNameService gameNameService;

    @BeforeEach
    public void setup() {
        gameNameService = new GameNameService();
    }

    @Test
    public void testGetGameNameByPosition() {
        assertThat(gameNameService.getGameNameByPosition(1))
                .isEqualTo("Star Trek: The Next Generation");
        assertThat(gameNameService.getGameNameByPosition(7))
                .isEqualTo("The Who's Tommy Pinball Wizard");
    }

    @Test
    public void testGetGameNameByArenaId() {
        assertThat(gameNameService.getGameNameByArenaId(154968))
                .isEqualTo("Star Trek: The Next Generation");
        assertThat(gameNameService.getGameNameByArenaId(161602))
                .isEqualTo("The Who's Tommy Pinball Wizard");
    }

    @Test
    public void testInvalidPosition() {
        assertThatThrownBy(() -> gameNameService.getGameNameByPosition(8))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid position: 8");
    }

    @Test
    public void testInvalidArenaId() {
        assertThatThrownBy(() -> gameNameService.getGameNameByArenaId(999999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid arenaId: 999999");
    }
}
