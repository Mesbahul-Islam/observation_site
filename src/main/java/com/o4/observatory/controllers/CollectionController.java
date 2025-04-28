package com.o4.observatory.controllers;

import com.o4.observatory.models.Collection;
import com.o4.observatory.models.User;
import com.o4.observatory.repositories.UserRepository;
import com.o4.observatory.services.CollectionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.o4.observatory.services.PDFExportService;
import com.itextpdf.text.DocumentException;

import java.io.ByteArrayInputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/collections")
public class CollectionController {

    private final CollectionService collectionService;
    private final UserRepository userRepository;
    private final PDFExportService pdfExportService;
    
    public CollectionController(CollectionService collectionService, UserRepository userRepository, PDFExportService pdfExportService) {
        this.collectionService = collectionService;
        this.userRepository = userRepository;
        this.pdfExportService = pdfExportService;
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
    public ResponseEntity<?> exportCollectionAsPdf(@PathVariable Long id, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Collection collection = collectionService.getCollectionById(id);
        
        // Verify authorization
        if (collection == null || !collection.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.badRequest().body("Collection not found or access denied");
        }
        
        try {
            ByteArrayInputStream pdfStream = pdfExportService.generateCollectionPDF(collection);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=" + sanitizeFilename(collection.getName()) + ".pdf");
            
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(pdfStream));
        } catch (DocumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating PDF: " + e.getMessage());
        }
    }

    // Helper method to sanitize filename
    private String sanitizeFilename(String input) {
        return input.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
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
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        User currentUser = getCurrentUser(authentication);
        boolean added = collectionService.addObservationToCollection(collectionId, observationId, currentUser);

        //for confirmation message
        if (!added) {
            redirectAttributes.addFlashAttribute("message", "This observation is already in the collection.");
            redirectAttributes.addFlashAttribute("messageType", "warning");
        } else {
            redirectAttributes.addFlashAttribute("message", "Observation added to collection successfully.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        }
        
        return "redirect:/observations/" + observationId;
    }
}