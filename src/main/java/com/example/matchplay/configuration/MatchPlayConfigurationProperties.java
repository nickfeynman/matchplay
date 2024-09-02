package com.example.matchplay.configuration;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(MatchPlayConfigurationProperties.CONFIG_PREFIX)
public class MatchPlayConfigurationProperties {

    public static final String CONFIG_PREFIX = "matchplay";

    private Integer tournamentId;

    public Integer getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Integer tournamentId) {
        this.tournamentId = tournamentId;
    }
}