package com.example.matchplay.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Standing(
        @JsonProperty("playerId") int playerId,
        @JsonProperty("position") int position,
        @JsonProperty("points") String points,
        @JsonProperty("pointsWithTiebreaker") String pointsWithTiebreaker,
        @JsonProperty("gamesPlayed") int gamesPlayed,
        @JsonProperty("strikeCount") int strikeCount,
        @JsonProperty("adjustment") int adjustment,
        @JsonProperty("frenzyWins") Integer frenzyWins,
        @JsonProperty("frenzyLosses") Integer frenzyLosses,
        @JsonProperty("tiebreakers") List<String> tiebreakers,
        @JsonProperty("activeGames") List<String> activeGames,
        @JsonProperty("activeGameColor") String activeGameColor
) {}
