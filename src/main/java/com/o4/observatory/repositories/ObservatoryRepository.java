package com.o4.observatory.repositories;

import com.o4.observatory.models.Observatory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObservatoryRepository extends JpaRepository<Observatory, Long> {
    Observatory findByName(String name);
}