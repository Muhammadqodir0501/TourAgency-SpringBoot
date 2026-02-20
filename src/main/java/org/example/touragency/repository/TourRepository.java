package org.example.touragency.repository;

import org.example.touragency.model.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
@Repository
public interface TourRepository extends JpaRepository<Tour,Long> {

    Optional<Tour> findById(UUID id);

    List<Tour> findAllByAgencyId(UUID agencyId);

    List<Tour> findAll();

    void deleteByAgencyId(UUID agencyId);






}
