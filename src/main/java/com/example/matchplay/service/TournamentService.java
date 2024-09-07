package com.example.matchplay.service;

import com.example.matchplay.api.RoundDisplay;
import com.example.matchplay.api.StandingDisplay;

import java.util.List;

public interface TournamentService {

    List<StandingDisplay> getStandings();

    RoundDisplay getLatestRoundForActivePinId();

    void setActivePinId(Integer pinId);
}
