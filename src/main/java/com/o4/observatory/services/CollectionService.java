package com.o4.observatory.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.o4.observatory.models.Collection;
import com.o4.observatory.models.Observation;
import com.o4.observatory.models.User;
import com.o4.observatory.repositories.CollectionRepository;
import com.o4.observatory.repositories.ObservationRepository;
import com.o4.observatory.repositories.UserRepository;

@Service
public class CollectionService {
    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final ObservationRepository observationRepository;

    public CollectionService(CollectionRepository collectionRepository, UserRepository userRepository, ObservationRepository observationRepository) {
        this.collectionRepository = collectionRepository;
        this.userRepository = userRepository;
        this.observationRepository = observationRepository;
    }
    public void createCollection(String name, String description, User user) {
        Collection collection = new Collection();
        collection.setName(name);
        collection.setDescription(description);
        collection.setUser(user);
        collectionRepository.save(collection);
    }

    public Collection getCollectionById(Long id) {
        return collectionRepository.findById(id).orElse(null);
    }

    public List<Collection> getUserCollections(User user) {
        return collectionRepository.findByUser(user);
    }

    public void deleteCollection(Long id) {
        collectionRepository.deleteById(id);
    }
    public void updateCollection(Long id, String name, String description) {
        Collection collection = collectionRepository.findById(id).orElse(null);
        if (collection != null) {
            collection.setName(name);
            collection.setDescription(description);
            collectionRepository.save(collection);
        }
    }
    public List<Collection> getAllCollections() {
        return collectionRepository.findAll();
    }
    public void addObservationToCollection(Long collectionId, Long observationId, User user) {
        
        // Check if the user owns the collection
        Collection collection = collectionRepository.findById(collectionId).orElse(null);
        if (collection != null && collection.getUser().getId().equals(user.getId())) {
            // Check if the observation exists
            Observation observation = observationRepository.findById(observationId).orElse(null);
            if (observation != null) {
                // Add the observation to the collection
                collection.getObservations().add(observation);
                collectionRepository.save(collection);
            }
        }
    }
    public void removeObservationFromCollection(Long collectionId, Long observationId, User user) {
        Collection collection = collectionRepository.findById(collectionId).orElse(null);
        if(collection != null && collection.getUser().getId().equals(user.getId())) {
            Observation observation = observationRepository.findById(observationId).orElse(null);
            if (observation != null) {
                collection.getObservations().remove(observation);
                collectionRepository.save(collection);
            }
        }
    }

    public List<Collection> getCollectionsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            return collectionRepository.findByUser(user);
        }
        return null;
    }
    
}
