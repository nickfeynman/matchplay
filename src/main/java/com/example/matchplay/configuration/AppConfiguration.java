package com.example.matchplay.configuration;

import com.example.matchplay.api.GamesApi;
import com.example.matchplay.api.MatchPlayApi;
import com.example.matchplay.api.SinglePlayerGameApi;
import com.example.matchplay.api.StandingsApi;
import com.example.matchplay.api.TournamentApi;
import com.example.matchplay.api.UserApi;
import com.example.matchplay.service.MatchPlayTournamentService;
import com.example.matchplay.service.TournamentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;

@Configuration
@EnableConfigurationProperties({MatchPlayConfigurationProperties.class})
@EnableCaching
public class AppConfiguration {

    @Bean
    public TournamentService tournamentService(MatchPlayApi matchPlayApi,
                                               SinglePlayerGameApi singlePlayerGameApi,
                                               MatchPlayConfigurationProperties matchPlayConfigurationProperties) {
        return new MatchPlayTournamentService(matchPlayApi,
                singlePlayerGameApi,
                matchPlayConfigurationProperties);
    }

    @Bean
    public RestClient restClient(RestClient.Builder restClientBuilder, @Value("${matchplay.api-key}") String apiKey) {
        return  restClientBuilder.baseUrl("https://app.matchplay.events/api/")
                .defaultHeaders(h -> {
                    h.setBearerAuth(apiKey);
                    h.setContentType(MediaType.APPLICATION_JSON);
                    h.setAccept(List.of(MediaType.APPLICATION_JSON));
                })
                .build();
    }

    @Bean
    public MatchPlayApi matchPlayApi(RestClient restClient, GamesApi gamesApi, UserApi userApi, StandingsApi standingsApi,
                                     TournamentApi tournamentApi) {
        return new MatchPlayApi(restClient, gamesApi, userApi, standingsApi, tournamentApi);
    }

    @Bean
    public GamesApi gamesApi(RestClient restClient) {
        return new GamesApi(restClient);
    }

    @Bean
    public UserApi userApi(RestClient restClient) {
        return new UserApi(restClient);
    }

    @Bean
    public StandingsApi standingsApi(RestClient restClient) {
        return new StandingsApi(restClient);
    }


    @Bean
    public TournamentApi tournamentApi(RestClient restClient) {
        return new TournamentApi(restClient);
    }
}
