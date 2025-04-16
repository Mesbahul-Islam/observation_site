package com.o4.observatory.controllers;

import com.o4.observatory.services.ObservationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    private final ObservationService observationService;
    
    public HomeController(ObservationService observationService) {
        this.observationService = observationService;
    }
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("popularObservations", observationService.getTopObservations());
        model.addAttribute("mostCommented", observationService.getMostCommentedObservation());
        model.addAttribute("highestRated", observationService.getHighestRatedObservation());
        return "index";
    }
}