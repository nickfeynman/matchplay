package com.example.matchplay;

import java.util.List;

public interface TournamentService {

    List<TournamentStanding> getStandings(String tournamentId);


}
