package com.example.matchplay.controller;

import com.example.matchplay.api.RoundDisplay;
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

@Controller
public class MatchPlayController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private TournamentService tournamentService;

    @Autowired
    public MatchPlayController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
        logger.info("Version 2");
    }


    @GetMapping("/greeting")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    @GetMapping("/standings")
    public String standings(Model model) {
        logger.info("called standings...");
        var tournamentStandings = tournamentService.getStandings();
        model.addAttribute("standings", tournamentStandings);
        return "standings";
    }

    @GetMapping("/round")
    public String round(Model model) {

        logger.info("called round...");
        //Integer tournyId = 149146; //149145;
        RoundDisplay roundDisplay = this.tournamentService.getLatestRoundForActivePinId();
//		Map<String, List<RoundDisplay>> roundDisplays = matchPlayApi.getRoundDisplay(tournyId);
//		RoundDisplay roundDisplay = this.matchPlayApi.getLatestRoundForGame("The Shadow", roundDisplays);
        model.addAttribute("roundDisplay", roundDisplay);
        return "round";
    }

    @PostMapping("/active-pin-id")
    public ResponseEntity<String> processInteger(@Valid @RequestBody @NotNull Integer pinId) {
        this.tournamentService.setActivePinId(pinId);
        // Return a meaningful response
        return ResponseEntity.ok("Processed value: " + pinId);
    }

}