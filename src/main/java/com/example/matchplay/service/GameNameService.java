package com.example.matchplay.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Converts the index of tile pressed on the kiosk to a game name.
 */
public class GameNameService {

    private final Map<Integer, String> arenaToName = Map.of(
            154968, "Star Trek: The Next Generation",
            161605, "Indianapolis 500",
            154983, "Whirlwind",
            161604, "Pulp Fiction (LE)",
            161603, "Congo",
            154978, "The Shadow",
            161602, "The Who's Tommy Pinball Wizard"
    );

    private static final List<String> orderedGameNames = List.of(
            "Star Trek: The Next Generation",
            "Indianapolis 500",
            "Whirlwind",
            "Pulp Fiction (LE)",
            "Congo",
            "The Shadow",
            "The Who's Tommy Pinball Wizard"
    );

    public static boolean contains(String name) {
        return List.of(
                "Star Trek: The Next Generation",
                "Indianapolis 500",
                "Whirlwind",
                "Pulp Fiction (LE)",
                "Congo",
                "The Shadow",
                "The Who's Tommy Pinball Wizard"
        ).contains(name);
    }

    public static List<String> getAllGameNames() {
        return orderedGameNames;
    }

    public String getGameNameByPosition(int position) {
        int index = position - 1;
        if (index < 0 || index >= orderedGameNames.size()) {
            throw new IllegalArgumentException("Invalid position: " + position);
        }
        return orderedGameNames.get(index);
    }

    public String getGameNameByArenaId(int arenaId) {
        if (arenaId == 80475) {
            return "Medieval Madness";
        }
        String name = arenaToName.get(arenaId);
        if (name == null) {
            throw new IllegalArgumentException("Invalid arenaId: " + arenaId);
        }
        return name;
    }

    public int getArenaIdByPosition(int position) {
        String gameName = getGameNameByPosition(position);
        if (gameName.equals("Medieval Madness")) {
            return 80475;
        }
        return getArenaIdForGame(gameName);
    }

    public Integer getArenaIdForGame(String gameName) {
        return arenaToName.entrySet().stream()
                .filter(entry -> entry.getValue().equals(gameName))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No arena ID found for game: " + gameName));
    }


}
