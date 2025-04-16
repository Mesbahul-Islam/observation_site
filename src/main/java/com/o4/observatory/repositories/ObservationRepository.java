package com.o4.observatory.repositories;

import com.o4.observatory.models.Observation;
import com.o4.observatory.models.User;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ObservationRepository extends JpaRepository<Observation, Long> {
    List<Observation> findByUser(User user);

    List<Observation> findByIdAndCreatedAtBetweenAndUser(Long id, LocalDateTime start, LocalDateTime end, User user);
    
    @Query(value = "SELECT o FROM Observation o ORDER BY o.viewCount DESC")
    Page<Observation> findAllOrderByViewCountDesc(org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT o FROM Observation o LEFT JOIN o.comments c GROUP BY o.id ORDER BY COUNT(c) DESC LIMIT 1")
    Observation findMostCommented();
    
    @Query("SELECT o FROM Observation o LEFT JOIN o.ratings r GROUP BY o.id ORDER BY AVG(r.value) DESC LIMIT 1")
    Observation findHighestRated();

    @Query("SELECT o FROM Observation o ORDER BY o.viewCount DESC")
    List<Observation> findTopByViewCount(org.springframework.data.domain.Pageable pageable);

    //apparently you have to use naming conventions for the method names
    //this is stupid
    //edit: actually this is genius
    List<Observation> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Observation> findByCreatedAtBetweenAndUser(LocalDateTime start, LocalDateTime end, User user);
    List<Observation> findByCreatedAtAfterAndUser(LocalDateTime start, User user);
    List<Observation> findByCreatedAtAfter(LocalDateTime start);
    List<Observation> findByCreatedAtBeforeAndUser(LocalDateTime end, User user);
    List<Observation> findByCreatedAtBefore(LocalDateTime end);
    List<Observation> findByNameContainingIgnoreCase(String name);
    List<Observation> findByNameContainingIgnoreCaseAndUser(String name, User user);
    List<Observation> findByNameContainingIgnoreCaseAndCreatedAtBetween(String name, LocalDateTime start, LocalDateTime end);
    List<Observation> findByNameContainingIgnoreCaseAndCreatedAtBetweenAndUser(String name, LocalDateTime start, LocalDateTime end, User user);
    List<Observation> findByNameContainingIgnoreCaseAndCreatedAtAfter(String name, LocalDateTime start);
    List<Observation> findByNameContainingIgnoreCaseAndCreatedAtAfterAndUser(String name, LocalDateTime start, User user);
    List<Observation> findByNameContainingIgnoreCaseAndCreatedAtBefore(String name, LocalDateTime end);
    List<Observation> findByNameContainingIgnoreCaseAndCreatedAtBeforeAndUser(String name, LocalDateTime end, User user);
    
    

}