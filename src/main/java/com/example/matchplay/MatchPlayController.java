package com.example.matchplay;

import com.example.matchplay.domain.RoundDisplay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class MatchPlayController {

	private TournamentService tournamentService;
	private MatchPlayApi matchPlayApi;

	@Autowired
	public MatchPlayController(TournamentService tournamentService, MatchPlayApi matchPlayApi) {
		this.tournamentService = tournamentService;
		this.matchPlayApi = matchPlayApi;
		System.out.println("version 2");
	}


	@GetMapping("/greeting")
	public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
		model.addAttribute("name", name);
		return "greeting";
	}

	@GetMapping("/standings")
	public String standings(Model model) {
		System.out.println("called standings...");
		var tournamentStandings = tournamentService.getStandings("1");
		model.addAttribute("standings", tournamentStandings);
		return "standings";
	}

	@GetMapping("/round")
	public String round(Model model) {
		System.out.println("called round...");
		Integer tournyId = 149146; //149145;
		Map<String, List<RoundDisplay>> roundDisplays = matchPlayApi.getRoundDisplay(tournyId);
		List<RoundDisplay> roundDisplay = MatchPlayApi.getLatestRoundForMachine("The Shadow", roundDisplays);
		model.addAttribute("roundDisplay", roundDisplay.get(0));
		return "round";
	}

}
