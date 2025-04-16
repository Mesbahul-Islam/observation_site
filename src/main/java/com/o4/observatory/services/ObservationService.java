package com.o4.observatory.services;

import com.o4.observatory.models.Comment;
import com.o4.observatory.models.Observation;
import com.o4.observatory.models.Observatory;
import com.o4.observatory.models.Rating;
import com.o4.observatory.models.User;
import com.o4.observatory.repositories.CommentRepository;
import com.o4.observatory.repositories.ObservationRepository;
import com.o4.observatory.repositories.ObservatoryRepository;
import com.o4.observatory.repositories.RatingRepository;
import com.o4.observatory.repositories.UserRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ObservationService {

    private final CommentRepository commentRepository;
    
    private final ObservationRepository observationRepository;
    private final ObservatoryRepository observatoryRepository;
    private final RatingRepository ratingRepository;
    
    private final UserRepository userRepository;

    public ObservationService(ObservationRepository observationRepository, 
                             ObservatoryRepository observatoryRepository,
                             UserRepository userRepository,
                             CommentRepository commentRepository,
                             RatingRepository ratingRepository) {
        this.observationRepository = observationRepository;
        this.observatoryRepository = observatoryRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.ratingRepository = ratingRepository;
    }
    
    public Observation createObservation(String name, String description, 
                                        double rightAscension, double declination,
                                        String observatoryName, double latitude, double longitude,
                                        User currentUser) {
        
        Observatory observatory = observatoryRepository.findByName(observatoryName);
        if (observatory == null) {
            observatory = new Observatory();
            observatory.setName(observatoryName);
            observatory.setLatitude(latitude);
            observatory.setLongitude(longitude);
            observatory = observatoryRepository.save(observatory);
        }
        
        Observation observation = new Observation();
        observation.setName(name);
        observation.setDescription(description);
        observation.setRightAscension(rightAscension);
        observation.setDeclination(declination);
        observation.setObservatory(observatory);
        observation.setUser(currentUser);
        observation.setCreatedAt(LocalDateTime.now());
        
        return observationRepository.save(observation);
    }
    
    //get all observations
    public List<Observation> getAllObservations() {
        return observationRepository.findAll();
    }
    
    // Get observation by ID
    public Observation getObservationById(Long id) {
        return observationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Observation not found")); //very convenient
    }
    
    // Increment view count when observation is accessed
    @Transactional
    public Observation viewObservation(Long id) {
        Observation observation = getObservationById(id);
        observation.incrementViewCount();
        return observationRepository.save(observation);
    }
    
    //don't really understand this
    public List<Observation> getTopObservations() {
        return observationRepository.findTopByViewCount(PageRequest.of(0, 5));
    }
    
    //search observations
    public List<Observation> searchObservations(String name, LocalDateTime startDate, 
                                            LocalDateTime endDate, String username) {
        User user = null;
        if (username != null && !username.isEmpty()) {
            try {
                user = userRepository.findByUsername(username)
                    .orElse(null);
            } catch (Exception e) {
                // User not found, but we'll handle it below
            }
        }

        // If username was provided but user doesn't exist
        if (username != null && !username.isEmpty() && user == null) {
            return Collections.emptyList();
        }

        // Handle all the search combinations
        if (name != null && !name.isEmpty()) {
            if (startDate != null && endDate != null) {
                if (user != null) {
                    return observationRepository.findByNameContainingIgnoreCaseAndCreatedAtBetweenAndUser(
                        name, startDate, endDate, user);
                } else {
                    return observationRepository.findByNameContainingIgnoreCaseAndCreatedAtBetween(
                        name, startDate, endDate);
                }
            } else if (startDate != null) {
                if (user != null) {
                    return observationRepository.findByNameContainingIgnoreCaseAndCreatedAtAfterAndUser(
                        name, startDate, user);
                } else {
                    return observationRepository.findByNameContainingIgnoreCaseAndCreatedAtAfter(
                        name, startDate);
                }
            } else if (endDate != null) {
                if (user != null) {
                    return observationRepository.findByNameContainingIgnoreCaseAndCreatedAtBeforeAndUser(
                        name, endDate, user);
                } else {
                    return observationRepository.findByNameContainingIgnoreCaseAndCreatedAtBefore(
                        name, endDate);
                }
            } else {
                // Only name (and possibly user) is provided
                if (user != null) {
                    return observationRepository.findByNameContainingIgnoreCaseAndUser(name, user);
                } else {
                    return observationRepository.findByNameContainingIgnoreCase(name);
                }
            }
        } else {
            // Name is not provided, so use the existing date/user filters
            if (startDate != null && endDate != null) {
                if (user != null) {
                    return observationRepository.findByCreatedAtBetweenAndUser(startDate, endDate, user);
                } else {
                    return observationRepository.findByCreatedAtBetween(startDate, endDate);
                }
            } else if (startDate != null) {
                if (user != null) {
                    return observationRepository.findByCreatedAtAfterAndUser(startDate, user);
                } else {
                    return observationRepository.findByCreatedAtAfter(startDate);
                }
            } else if (endDate != null) {
                if (user != null) {
                    return observationRepository.findByCreatedAtBeforeAndUser(endDate, user);
                } else {
                    return observationRepository.findByCreatedAtBefore(endDate);
                }
            } else if (user != null) {
                // Only user is provided
                return observationRepository.findByUser(user);
            }
        }
        
        // No criteria provided, return all observations
        return getAllObservations();
    }
    
    // Update an observation
    public Observation updateObservation(Long id, String name, String description, 
                                        double rightAscension, double declination,
                                        String modificationReason, User currentUser) {
        
        Observation observation = getObservationById(id);
        
        // Verify the current user is the owner
        if (!observation.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only edit your own observations");
        }
        
        observation.setName(name);
        observation.setDescription(description);
        observation.setRightAscension(rightAscension);
        observation.setDeclination(declination);
        observation.setModifiedAt(LocalDateTime.now());
        observation.setModificationReason(modificationReason);
        
        return observationRepository.save(observation);
    }
    
    // Delete an observation
    public void deleteObservation(Long id, User currentUser) {
        Observation observation = getObservationById(id);
        
        // Verify the current user is the owner
        if (!observation.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own observations");
        }
        
        observationRepository.delete(observation);
    }
    
    // Get most commented observation
    public Observation getMostCommentedObservation() {
        return observationRepository.findMostCommented();
    }
    
    // Get highest rated observation
    public Observation getHighestRatedObservation() {
        return observationRepository.findHighestRated();
    }
    public Comment addComment(Long observationId, String content, User user) {
        Observation observation = getObservationById(observationId);
        Comment comment = new Comment();
        comment.setObservation(observation);
        comment.setContent(content);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());
        
        return commentRepository.save(comment);
    }
    public Rating rateObservation(Long observationId, int value, User user) {
        //check if value is valid
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("Rating value must be between 1 and 5");
        }
        Observation observation = getObservationById(observationId);

        //check if same user has already rated the observation
        Optional<Rating> existingRating = ratingRepository.findByUserAndObservation(user, observation);
        
        if(existingRating.isPresent()){
            //update rating
            Rating rating = existingRating.get();
            rating.setValue(value);
            return ratingRepository.save(rating);
        }
        else{
            //create new rating
            Rating rating = new Rating();
            rating.setValue(value);
            rating.setUser(user);
            rating.setObservation(observation);
            return ratingRepository.save(rating);
        }
    }
}