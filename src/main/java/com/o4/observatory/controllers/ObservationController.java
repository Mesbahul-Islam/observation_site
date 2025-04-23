package com.o4.observatory.controllers;

import com.o4.observatory.models.Collection;
import com.o4.observatory.models.Observation;
import com.o4.observatory.models.User;
import com.o4.observatory.repositories.UserRepository;
import com.o4.observatory.services.CollectionService;
import com.o4.observatory.services.ObservationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/observations")
public class ObservationController {

    private final ObservationService observationService;
    private final UserRepository userRepository;
    private final CollectionService collectionService;
    
    public ObservationController(ObservationService observationService, UserRepository userRepository, CollectionService collectionService) {
        this.collectionService = collectionService;
        this.observationService = observationService;
        this.userRepository = userRepository;
    }
    
    // Get current logged-in user
    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    // List all observations
    @GetMapping
    public String listObservations(Model model) {
        model.addAttribute("observations", observationService.getAllObservations());
        return "observations/list";
    }
    
    // Show form to create a new observation
    @GetMapping("/new")
    public String newObservationForm() {
        return "observations/new";
    }
    
    // Process the creation of a new observation
    @PostMapping
    public String createObservation(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam double rightAscension,
            @RequestParam double declination,
            @RequestParam String observatoryName,
            @RequestParam double latitude,
            @RequestParam double longitude,
            Authentication authentication) {
        
        User currentUser = getCurrentUser(authentication);
        
        observationService.createObservation(
            name, description, rightAscension, declination,
            observatoryName, latitude, longitude, currentUser
        );
        
        return "redirect:/observations";
    }
    
    // View a single observation (increments view count)
    @GetMapping("/{id}")
    public String viewObservation(@PathVariable Long id, Model model, Authentication authentication) {
        Observation observation = observationService.viewObservation(id);
        model.addAttribute("observation", observation);

        if(authentication != null && authentication.isAuthenticated()) {
            User currentUser = getCurrentUser(authentication);
            List<Collection> userCollections = collectionService.getUserCollections(currentUser);
            model.addAttribute("userCollections", userCollections);

            System.out.println("User Collections: " + userCollections);
        }
        return "observations/view";
    }
    
    // Show form to edit an observation
    @GetMapping("/{id}/edit")
    public String editObservationForm(@PathVariable Long id, Model model, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Observation observation = observationService.getObservationById(id);
        
        // Check if the current user is the owner
        if (!observation.getUser().getId().equals(currentUser.getId())) {
            return "redirect:/observations";
        }
        
        model.addAttribute("observation", observation);
        return "observations/edit";
    }
    
    // Process the update of an observation
    @PostMapping("/{id}")
    public String updateObservation(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam double rightAscension,
            @RequestParam double declination,
            @RequestParam String modificationReason,
            Authentication authentication) {
        
        User currentUser = getCurrentUser(authentication);
        
        observationService.updateObservation(
            id, name, description, rightAscension, declination,
            modificationReason, currentUser
        );
        
        return "redirect:/observations/" + id;
    }
    
    // Delete an observation
    @PostMapping("/{id}/delete")
    public String deleteObservation(@PathVariable Long id, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        observationService.deleteObservation(id, currentUser);
        return "redirect:/observations";
    }
    
    // Search observations
    @GetMapping("/search")
    public String searchObservations(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String username,
            Model model) {
        
        // Parse date parameters if provided
        LocalDateTime start = null;
        if (startDate != null && !startDate.isEmpty()) {
            try {
                start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME);
            } catch (Exception e) {
                // Handle invalid date format
                model.addAttribute("error", "Invalid start date format. Use ISO format (yyyy-MM-ddTHH:mm:ss)");
            }
        }
        
        LocalDateTime end = null;
        if (endDate != null && !endDate.isEmpty()) {
            try {
                end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME);
            } catch (Exception e) {
                // Handle invalid date format
                model.addAttribute("error", "Invalid end date format. Use ISO format (yyyy-MM-ddTHH:mm:ss)");
            }
        }
        
        // Call the service with the search criteria
        List<Observation> results = observationService.searchObservations(name, start, end, username);
        
        // Add search parameters to the model for form repopulation
        model.addAttribute("searchName", name);
        model.addAttribute("searchUsername", username);
        model.addAttribute("searchStartDate", startDate);
        model.addAttribute("searchEndDate", endDate);
        
        // Add the search results to the model
        model.addAttribute("observations", results);
        model.addAttribute("searchPerformed", true);
        
        return "observations/list";
    }


    @PostMapping("/{id}/comment")
    public String postMethodName(@PathVariable Long id, @RequestParam String content, Authentication authentication) {
        User user = getCurrentUser(authentication);
        observationService.addComment(id, content, user);
        return "redirect:/observations/" + id;
    }
    
    //rating logic
    @PostMapping("/{id}/rate")
    public String rateObservation(@PathVariable Long id, @RequestParam int rating, Authentication authentication) {
        User user = getCurrentUser(authentication);
        observationService.rateObservation(id, rating, user);
        return "redirect:/observations/" + id;
    }  

}