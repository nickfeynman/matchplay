package com.example.matchplay.service;

import com.example.matchplay.api.BestScoresDisplay;
import com.example.matchplay.api.RoundDisplay;
import com.example.matchplay.api.StandingDisplay;

import java.util.List;

public interface TournamentService {

    List<StandingDisplay> getStandings();

    RoundDisplay getLatestRoundForActivePinId();

    List<BestScoresDisplay> getBestScoresForActivePinId();

    List<BestScoresDisplay> getBestScoresForArena(Integer tournamentId, Integer arenaId);

    void setActivePinId(Integer pinId);
}
