package com.example.matchplay.api;

import java.util.List;

public record RoundDisplay(String name, String status, String gameName, List<String> userNames, List<String> points) {
}
