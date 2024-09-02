package com.example.matchplay.service;

import java.util.Arrays;
import java.util.List;

public class GameNameService {

    /***
     * These are the names of the games as defined by MatchPlay
     */
    public static List<String> GAME_NAMES = Arrays.asList(
            "Star Trek: The Next Generation",
            "Deadpool (Pro)",
            "Whirlwind",
            "Pulp Fiction (SE)",
            "Congo",
            "The Shadow",
            "Spectrum",
            "Cactus Canyon (Remake Special/LE)",
            "The Who's Tommy Pinball Wizard");


    public String getGameName(int position) {
        // Adjust for zero-based indexing
        int index = position - 1;

        // Check if the position is valid
        if (index < 0 || index >= GAME_NAMES.size()) {
            throw new IllegalArgumentException("Invalid position: " + position);
        }

        // Return the name at the adjusted position
        return GAME_NAMES.get(index);
    }


}
