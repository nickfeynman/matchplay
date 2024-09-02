package com.example.matchplay.api;

import java.util.List;

public record RoundDetails(Integer roundId, Integer gameId, List<Integer> userIds, List<Float> resultPoints) {
}
