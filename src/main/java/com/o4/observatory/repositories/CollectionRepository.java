package com.o4.observatory.repositories;

import com.o4.observatory.models.Collection;
import com.o4.observatory.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findByUser(User user);
}