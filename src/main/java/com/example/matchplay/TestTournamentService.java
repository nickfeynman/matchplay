package com.example.matchplay;

import java.util.ArrayList;
import java.util.List;

public class TestTournamentService implements TournamentService {

    @Override
    public List<TournamentStanding> getStandings(String tournamentId) {
        return fakeData();
    }

    private List<TournamentStanding> fakeData() {
        List<TournamentStanding> standings = new ArrayList<>();
        standings.add(new TournamentStanding("David Patlakh", "26 pts.", "26:3:1"));
        standings.add(new TournamentStanding("Matthew Grady", "23 pts.", "23:3:0"));
        standings.add(new TournamentStanding("Dan Merrill", "23 pts.", "23:2:2"));
        standings.add(new TournamentStanding("Nick Berry", "23 pts.", "23:2:2"));
        standings.add(new TournamentStanding("Jeff Anderson NJ", "23 pts.", "23:2:2"));
        standings.add(new TournamentStanding("Ali Bisset", "20 pts.", "20:2:1"));
        standings.add(new TournamentStanding("Zac Till", "20 pts.", "20:1:3"));
        standings.add(new TournamentStanding("Nint Hoo", "17 pts.", "17:0:4"));
        standings.add(new TournamentStanding("Dennis Wiener", "5 pts.", "5:0:0"));
        return standings;
    }

}
