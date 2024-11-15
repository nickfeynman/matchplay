package com.example.matchplay.controller;

import com.example.matchplay.api.BestScoresDisplay;
import com.example.matchplay.api.RoundDisplay;
import com.example.matchplay.api.StandingDisplay;
import com.example.matchplay.service.TournamentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MatchPlayController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private TournamentService tournamentService;

    private SseController sseController;

    @Autowired
    public MatchPlayController(TournamentService tournamentService, SseController sseController) {
        this.tournamentService = tournamentService;
        this.sseController = sseController;
        logger.info("Version 2");
    }


    @GetMapping("/greeting")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    @GetMapping("/standings")
    public String standings(Model model) {
        logger.debug("calling standings...");
        List<StandingDisplay> standingDisplays = tournamentService.getStandings();
        model.addAttribute("standings", standingDisplays);
        logger.debug("called standings");
        return "standings";
    }

    @GetMapping("/round")
    public String round(Model model) {
        logger.debug("calling round...");
        //Integer tournyId = 149146; //149145;
        RoundDisplay roundDisplay = this.tournamentService.getLatestRoundForActivePinId();
//		Map<String, List<RoundDisplay>> roundDisplays = matchPlayApi.getRoundDisplay(tournyId);
//		RoundDisplay roundDisplay = this.matchPlayApi.getLatestRoundForGame("The Shadow", roundDisplays);
        model.addAttribute("roundDisplay", roundDisplay);
        logger.debug("called round");
        return "round";
    }

    @GetMapping("/bestscores")
    public String bestScores(Model model) {
        logger.debug("calling bestscores...");
        List<BestScoresDisplay> bestScores = this.tournamentService.getBestScoresForActivePinId();
        model.addAttribute("bestScores", bestScores);
        logger.debug("called bestscores");
        return "bestscores";
    }

    @PostMapping("/active-pin-id")
    public ResponseEntity<String> processInteger(@Valid @RequestBody @NotNull Integer pinId) {
        logger.info("Changing active pin id to " + pinId);
        this.tournamentService.setActivePinId(pinId);
        logger.info("Refreshing browser");
        this.sseController.sendSSEEvent("refresh");
        logger.info("Changed");
        return ResponseEntity.ok("Processed value: " + pinId);
    }

}
