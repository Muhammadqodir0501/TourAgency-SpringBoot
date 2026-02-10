package org.example.touragency.repository;

import org.example.touragency.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository  extends JpaRepository<Booking, UUID> {

     Optional<Booking> findByUserIdAndTourId(UUID userId, UUID tourId);

     List<Booking> findByUserId(UUID userId);

     void deleteByTourId(UUID tourId);

     void deleteByUserIdAndTourId(UUID userId, UUID tourId);

     void deleteByUserId(UUID userId);


}
