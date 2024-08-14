package com.example.matchplay.configuration;

import com.example.matchplay.MatchPlayApi;
import com.example.matchplay.TestTournamentService;
import com.example.matchplay.TournamentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfiguration {

    @Bean
    public TournamentService tournamentService() {
        return new TestTournamentService();
    }

    @Bean
    public MatchPlayApi matchPlayApi(RestClient.Builder restClientBuilder) {
        return new MatchPlayApi(restClientBuilder);
    }
//    @Bean
//    public MatchPlayService matchPlayService(@Value("${bearer.token}") String bearerToken) {
//        return new MatchPlayService(bearerToken);
//    }
}
