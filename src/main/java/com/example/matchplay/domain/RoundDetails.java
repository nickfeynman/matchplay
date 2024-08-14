package com.example.matchplay.domain;

import java.util.List;

public record RoundDetails(Integer roundId, Integer gameId, List<Integer> userIds, List<Float> resultPoints) {
}
