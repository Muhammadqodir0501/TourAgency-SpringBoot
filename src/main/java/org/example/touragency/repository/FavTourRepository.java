package org.example.touragency.repository;


import org.example.touragency.model.entity.FavouriteTour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavTourRepository extends JpaRepository<FavouriteTour, UUID> {

    List<FavouriteTour> findByUserId(UUID userId);

    Optional<FavouriteTour> findByUserIdAndTourId(UUID userId, UUID tourId);

    void deleteByTourId(UUID tourId);

    void deleteByUserIdAndTourId(UUID userId, UUID tourId);

    void deleteByUserId(UUID userId);

}
