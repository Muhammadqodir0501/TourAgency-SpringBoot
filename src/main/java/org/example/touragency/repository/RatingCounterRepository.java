package org.example.touragency.repository;

import org.example.touragency.model.entity.RatingCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingCounterRepository extends JpaRepository<RatingCounter, UUID> {

    Optional<RatingCounter> findByTourId(UUID tourId);

    void deleteByTourId(UUID tourId);
}
