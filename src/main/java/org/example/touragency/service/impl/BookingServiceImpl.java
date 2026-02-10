package org.example.touragency.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.touragency.dto.response.BookingResponseDto;
import org.example.touragency.exception.ConflictException;
import org.example.touragency.exception.NotFoundException;
import org.example.touragency.model.entity.Booking;
import org.example.touragency.model.entity.Tour;
import org.example.touragency.model.entity.User;
import org.example.touragency.repository.BookingRepository;
import org.example.touragency.repository.TourRepository;
import org.example.touragency.repository.UserRepository;
import org.example.touragency.service.abstractions.BookingService;
import org.example.touragency.service.abstractions.TourService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TourRepository tourRepository;
    private final TourService tourService;

    @Override
    @Transactional
    public BookingResponseDto addBooking(UUID userId, UUID tourId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new NotFoundException("Tour not found"));

        if (!tour.isAvailable()) {
            throw new ConflictException("Tour not available");
        }

        boolean alreadyBooked =
                bookingRepository.findByUserIdAndTourId(userId, tourId).isPresent();

        if (alreadyBooked) {
            throw new ConflictException("User already booked this tour");
        }

        Booking booking = Booking.builder()
                .user(user)
                .tour(tour)
                .build();

        bookingRepository.save(booking);
        tourService.tourIsBooked(tour);

        return new BookingResponseDto(
                booking.getId(),
                user.getId(),
                tour.getId()
        );
    }


    @Override
    public List<BookingResponseDto> getUsersBookings(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Booking> bookings = bookingRepository.findByUserId(user.getId());

        return bookings.stream()
                .map(b -> new BookingResponseDto(
                        b.getId(),
                        b.getUser().getId(),
                        b.getTour().getId()
                ))
                .toList();
    }


    @Override
    @Transactional
    public void cancelBooking(UUID userId, UUID tourId) {

        bookingRepository.findByUserIdAndTourId(userId, tourId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        tourService.tourBookingIsCanceled(tourId);
        bookingRepository.deleteByUserIdAndTourId(userId, tourId);
    }

}
