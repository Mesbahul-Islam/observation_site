package com.o4.observatory.repositories;

import com.o4.observatory.models.Rating;
import com.o4.observatory.models.Observation;
import com.o4.observatory.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserAndObservation(User user, Observation observation);
    
    @Query("SELECT AVG(r.value) FROM Rating r WHERE r.observation = ?1")
    Double getAverageRatingForObservation(Observation observation);
}