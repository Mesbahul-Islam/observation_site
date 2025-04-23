package com.o4.observatory.controllers;

import com.o4.observatory.models.Collection;
import com.o4.observatory.models.User;
import com.o4.observatory.repositories.UserRepository;
import com.o4.observatory.services.CollectionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/collections")
public class CollectionController {

    private final CollectionService collectionService;
    private final UserRepository userRepository;
    
    public CollectionController(CollectionService collectionService, UserRepository userRepository) {
        this.collectionService = collectionService;
        this.userRepository = userRepository;
    }
    
    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @GetMapping
    public String listCollections(Model model, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        model.addAttribute("collections", collectionService.getUserCollections(currentUser));
        return "collections/list";
    }
    
    @GetMapping("/new")
    public String newCollectionForm() {
        return "collections/new";
    }
    
    @PostMapping
    public String createCollection(
            @RequestParam String name,
            @RequestParam String description,
            Authentication authentication) {
        
        User currentUser = getCurrentUser(authentication);
        collectionService.createCollection(name, description, currentUser);
        return "redirect:/collections";
    }
    
    @GetMapping("/{id}")
    public String viewCollection(@PathVariable Long id, Model model, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Collection collection = collectionService.getCollectionById(id);
        
        if (!collection.getUser().getId().equals(currentUser.getId())) {
            return "redirect:/collections";
        }
        
        model.addAttribute("collection", collection);
        return "collections/view";
    }
    
    @GetMapping("/{collectionId}/add/{observationId}")
    public String addToCollection(
            @PathVariable Long collectionId,
            @PathVariable Long observationId,
            Authentication authentication) {
        
        User currentUser = getCurrentUser(authentication);
        collectionService.addObservationToCollection(collectionId, observationId, currentUser);
        return "redirect:/collections/" + collectionId;
    }
    
    @GetMapping("/{collectionId}/remove/{observationId}")
    public String removeFromCollection(
            @PathVariable Long collectionId,
            @PathVariable Long observationId,
            Authentication authentication) {
        
        User currentUser = getCurrentUser(authentication);
        collectionService.removeObservationFromCollection(collectionId, observationId, currentUser);
        return "redirect:/collections/" + collectionId;
    }
    
    @GetMapping("/{id}/pdf")
    public String exportCollectionAsPdf(@PathVariable Long id, Authentication authentication) {
        // Implement PDF export functionality
        return "redirect:/collections/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteCollection(@PathVariable Long id, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Collection collection = collectionService.getCollectionById(id);
        
        if (!collection.getUser().getId().equals(currentUser.getId())) {
            return "redirect:/collections";
        }
        
        collectionService.deleteCollection(id);
        return "redirect:/collections";
    }

    @PostMapping("/add-observation")
    public String addObservationToCollection(
            @RequestParam Long collectionId,
            @RequestParam Long observationId,
            Authentication authentication) {
        
        User currentUser = getCurrentUser(authentication);
        boolean added = collectionService.addObservationToCollection(collectionId, observationId, currentUser);
        
        return "redirect:/observations/" + observationId;
    }
}