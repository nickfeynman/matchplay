package com.example.matchplay.configuration;

import com.example.matchplay.api.MatchPlayApi;
import com.example.matchplay.service.MatchPlayTournamentService;
import com.example.matchplay.service.TournamentService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties({MatchPlayConfigurationProperties.class})
public class AppConfiguration {

    @Bean
    public TournamentService tournamentService(MatchPlayApi matchPlayApi, MatchPlayConfigurationProperties matchPlayConfigurationProperties) {
        //return new TestTournamentService();
        return new MatchPlayTournamentService(matchPlayApi, matchPlayConfigurationProperties);
    }

    @Bean
    public MatchPlayApi matchPlayApi(RestClient.Builder restClientBuilder) {
        return new MatchPlayApi(restClientBuilder);
    }

}
