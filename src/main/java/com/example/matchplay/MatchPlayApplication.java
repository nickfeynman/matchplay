package com.example.matchplay;

import com.example.matchplay.configuration.AppConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(AppConfiguration.class)
public class MatchPlayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatchPlayApplication.class, args);
    }

}
