package com.example.matchplay.service;

import com.example.matchplay.api.RoundDisplay;
import com.example.matchplay.api.TournamentStanding;

import java.util.List;

public interface TournamentService {

    List<TournamentStanding> getStandings();

    RoundDisplay getLatestRoundForActivePinId();

    void setActivePinId(Integer pinId);
}
