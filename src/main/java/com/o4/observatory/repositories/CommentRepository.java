package com.o4.observatory.repositories;

import com.o4.observatory.models.Comment;
import com.o4.observatory.models.Observation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByObservation(Observation observation);
}