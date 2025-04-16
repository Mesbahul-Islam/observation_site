package com.o4.observatory.controllers;

import com.o4.observatory.models.Observation;
import com.o4.observatory.models.User;
import com.o4.observatory.repositories.UserRepository;
import com.o4.observatory.services.ObservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


//remember to remove this 
@RestController
@RequestMapping("/api/observations")
public class ObservationRestController {

    private final ObservationService observationService;
    private final UserRepository userRepository;
    
    public ObservationRestController(ObservationService observationService, UserRepository userRepository) {
        this.observationService = observationService;
        this.userRepository = userRepository;
    }
    
    private User getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        return userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllObservations() {
        List<Observation> observations = observationService.getAllObservations();
        List<Map<String, Object>> response = observations.stream()
            .map(this::convertObservationToMap)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getObservation(@PathVariable Long id) {
        Observation observation = observationService.viewObservation(id);
        return ResponseEntity.ok(convertObservationToMap(observation));
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createObservation(
            @RequestBody Map<String, Object> payload,
            Authentication authentication) {
        
        User currentUser = getCurrentUser(authentication);
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        
        Observation observation = observationService.createObservation(
            (String) payload.get("name"),
            (String) payload.get("description"),
            ((Number) payload.get("rightAscension")).doubleValue(),
            ((Number) payload.get("declination")).doubleValue(),
            (String) payload.get("observatoryName"),
            ((Number) payload.get("latitude")).doubleValue(),
            ((Number) payload.get("longitude")).doubleValue(),
            currentUser
        );
        
        return ResponseEntity.status(201).body(convertObservationToMap(observation));
    }
    
    // Helper method to convert Observation to Map
    private Map<String, Object> convertObservationToMap(Observation observation) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", observation.getId());
        result.put("name", observation.getName());
        result.put("description", observation.getDescription());
        result.put("rightAscension", observation.getRightAscension());
        result.put("declination", observation.getDeclination());
        
        // Add user details without including observations
        Map<String, Object> user = new HashMap<>();
        user.put("id", observation.getUser().getId());
        user.put("username", observation.getUser().getUsername());
        user.put("nickname", observation.getUser().getNickname());
        result.put("user", user);
        
        // Add observatory details
        if (observation.getObservatory() != null) {
            Map<String, Object> observatory = new HashMap<>();
            observatory.put("id", observation.getObservatory().getId());
            observatory.put("name", observation.getObservatory().getName());
            observatory.put("latitude", observation.getObservatory().getLatitude());
            observatory.put("longitude", observation.getObservatory().getLongitude());
            result.put("observatory", observatory);
        }
        
        // Add other fields
        result.put("createdAt", observation.getCreatedAt());
        if (observation.getModifiedAt() != null) {
            result.put("modifiedAt", observation.getModifiedAt());
            result.put("modificationReason", observation.getModificationReason());
        }
        result.put("viewCount", observation.getViewCount());
        
        return result;
    }
}