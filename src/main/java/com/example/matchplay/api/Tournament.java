package com.example.matchplay.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import java.util.List;


public record Tournament(@JsonProperty("data") TournamentData data) {

    public record TournamentData(
            long tournamentId,
            String name,
            String status,
            String type,
            ZonedDateTime startUtc,
            String startLocal,
            ZonedDateTime endUtc,
            String endLocal,
            ZonedDateTime completedAt,
            long organizerId,
            long locationId,
            Long seriesId,
            String description,
            List<List<Integer>> pointsMap,
            List<List<String>> tiebreakerPointsMap,
            boolean test,
            String timezone,
            String scorekeeping,
            String link,
            Long linkedTournamentId,
            Integer estimatedTgp,
            Integer prizePool,
            Organizer organizer,
            List<Player> players,
            String seeding,
            String firstRoundPairing,
            String pairing,
            String playerOrder,
            String playerOrderOpen,
            String arenaAssignment,
            int duration,
            int gamesPerRound,
            int playoffsCutoff,
            String playoffsCutoffText,
            String playoffsCutoffColor,
            String suggestions,
            String tiebreaker,
            String scoring
    ) {}

    public record Organizer(
            long userId,
            String name,
            String firstName,
            String lastName,
            int ifpaId,
            String role,
            String flag,
            String location,
            String pronouns,
            String initials,
            String avatar,
            String banner,
            String tournamentAvatar,
            ZonedDateTime createdAt
    ) {}

    public record Player(
            long playerId,
            String name,
            Integer ifpaId,
            String status,
            long organizerId,
            int claimedBy,
            TournamentPlayer tournamentPlayer
    ) {}

    public record TournamentPlayer(
            String status,
            int seed,
            int pointsAdjustment,
            String subscription,
            List<String> labels,
            String labelColor
    ) {}
}
