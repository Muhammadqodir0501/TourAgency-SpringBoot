package org.example.touragency.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.touragency.dto.event.SystemEvent;
import org.example.touragency.dto.request.TourAddDto;
import org.example.touragency.dto.response.TourResponseDto;
import org.example.touragency.dto.response.TourUpdateDto;
import org.example.touragency.enums.EventStatus;
import org.example.touragency.enums.EventType;
import org.example.touragency.exception.BadRequestException;
import org.example.touragency.exception.ConflictException;
import org.example.touragency.exception.ForbiddenException;
import org.example.touragency.exception.NotFoundException;
import org.example.touragency.enums.Role;
import org.example.touragency.model.entity.OutboxEvent;
import org.example.touragency.model.entity.Tour;
import org.example.touragency.model.entity.User;
import org.example.touragency.repository.*;
import org.example.touragency.security.SecurityUtils;
import org.example.touragency.service.abstractions.TourService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final FavTourRepository favTourRepository;
    private final BookingRepository bookingRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;




    @PreAuthorize("hasRole('AGENCY')")
    @Override
    @Transactional
    public TourResponseDto addNewTour(TourAddDto tourAddDto) {

        UUID currentAgencyId = SecurityUtils.getCurrentUserId();

        User agency = userRepository.findById(currentAgencyId)
                .orElseThrow(() -> new NotFoundException("Agency not found"));

        if (!agency.getRole().equals(Role.AGENCY)) {
            throw new ConflictException("User is not an agency");
        }

        int nights = calculatingNights(tourAddDto.getStartDate(), tourAddDto.getReturnDate());

        Tour newTour = Tour.builder()
                .title(tourAddDto.getTitle())
                .agency(agency)
                .city(tourAddDto.getCity())
                .hotel(tourAddDto.getHotel())
                .description(tourAddDto.getDescription())
                .startDate(tourAddDto.getStartDate())
                .returnDate(tourAddDto.getReturnDate())
                .nights(nights)
                .price(tourAddDto.getPrice())
                .seatsTotal(tourAddDto.getSeatsTotal())
                .seatsAvailable(tourAddDto.getSeatsTotal())
                .isAvailable(true)
                .views(0L)
                .build();

                 tourRepository.save(newTour);

        try{
            SystemEvent systemEvent = SystemEvent.builder()
                    .eventType(EventType.TOUR_CREATED)
                    .entityId(String.valueOf(newTour.getId()))
                    .userId(agency.getId())
                    .timestamp(LocalDateTime.now())
                    .payload(newTour)
                    .build();

            OutboxEvent outboxEvent = new OutboxEvent();
                    outboxEvent.setEventType(EventType.TOUR_CREATED);
                    outboxEvent.setStatus(EventStatus.PENDING);
                    outboxEvent.setPayload(objectMapper.writeValueAsString(systemEvent));
                    outboxEvent.setCreatedAt(LocalDateTime.now());

            outboxEventRepository.save(outboxEvent);

            log.info("Tour created and outbox event saved for tour ID: {}", newTour.getId());

        } catch(Exception e){
            log.error("Failed to save outbox event for tour ID: {}", newTour.getId(), e);
            throw new RuntimeException("Could not process tour creation", e);
        }

        return toResponseDto(newTour);

    }


    @PreAuthorize("hasRole('AGENCY')")
    @Override
    @Transactional
    public void deleteTour(UUID agencyId, UUID tourId) {
        Optional<User> agency = userRepository.findById(agencyId);

        if (agency.isEmpty()) {
            throw new NotFoundException("Agency not found");
        }

        if (!agency.get().getRole().equals(Role.AGENCY)) {
            throw new ForbiddenException("User is not an agency");
        }

        ratingRepository.deleteByTourId(tourId);
        ratingRepository.deleteByTourId(tourId);
        favTourRepository.deleteByTourId(tourId);
        bookingRepository.deleteByTourId(tourId);
        tourRepository.deleteById(tourId);

    }


    @PreAuthorize("hasRole('AGENCY')")
    @Override
    @Transactional
    public TourResponseDto updateTour(UUID agencyId, UUID tourId, TourUpdateDto tourUpdateDto) {

        Optional<Tour> existingTour = tourRepository.findById(tourId);

        if(existingTour.isEmpty()) {
            throw new NotFoundException("Tour not found");
        }

        if(!existingTour.get().getAgency().getId().equals(agencyId)) {
            throw new NotFoundException("Agency not found");
        }
        Integer nights = calculatingNights(tourUpdateDto.getStartDate(), tourUpdateDto.getReturnDate());

        existingTour.get().setTitle(tourUpdateDto.getTitle());
        existingTour.get().setDescription(tourUpdateDto.getDescription());
        existingTour.get().setCity(tourUpdateDto.getCity());
        existingTour.get().setPrice(tourUpdateDto.getPrice());
        existingTour.get().setSeatsTotal(tourUpdateDto.getSeatsTotal());
        existingTour.get().setSeatsAvailable(tourUpdateDto.getSeatsTotal());
        existingTour.get().setStartDate(tourUpdateDto.getStartDate());
        existingTour.get().setReturnDate(tourUpdateDto.getReturnDate());
        existingTour.get().setNights(nights);
        existingTour.get().setHotel(tourUpdateDto.getHotel());
        tourRepository.save(existingTour.get());
        return toResponseDto(existingTour.get());



    }

    @Override
    public List<TourResponseDto> getAllTours() {
        return tourRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public TourResponseDto getTourById(UUID userId, UUID tourId) {
        Optional<Tour> tour = tourRepository.findById(tourId);
        if(tour.isEmpty()) {
            throw new NotFoundException("Tour not found");
        }

        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        tour.get().setViews(tour.get().getViews() + 1L);
        return toResponseDto(tour.orElse(null));
    }

    @PreAuthorize("hasRole('AGENCY')")
    @Override
    public List<TourResponseDto> getAllToursByAgencyId(UUID agencyId) {

        User agency = userRepository.findById(agencyId)
                .orElseThrow(() -> new NotFoundException("Agency not found"));

        if(!agency.getRole().equals(Role.AGENCY)) {
            throw new ForbiddenException("User is not an agency");
        }

        return tourRepository.findAll().stream()
                .filter(tour -> tour.getAgency().getId().equals(agencyId))
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void tourIsBooked(Tour tour) {
        if(tour.isAvailable()) {
            tour.setSeatsAvailable(tour.getSeatsAvailable() - 1);
            if(tour.getSeatsAvailable() == 0){
                tour.setAvailable(false);
            }
            tourRepository.save(tour);
        }
    }
    @Override
    @Transactional
    public void tourBookingIsCanceled(UUID tourId) {
        Optional<Tour> tour = tourRepository.findById(tourId);

        if(tour.isEmpty()) {
            throw new NotFoundException("Tour not found");
        }

        if(tour.get().getSeatsAvailable() == 0){
            tour.get().setAvailable(true);
        }
        tour.get().setSeatsAvailable(tour.get().getSeatsAvailable() + 1);
        tourRepository.save(tour.get());
    }

    @PreAuthorize("hasRole('AGENCY')")
    @Override
    @Transactional
    public TourResponseDto addDiscount(UUID agencyId, UUID tourId, Integer discountPercent) {
        Optional<User> admin =  userRepository.findById(agencyId);
        Optional<Tour> tour = tourRepository.findById(tourId);

        if(tour.isEmpty() || admin.isEmpty()) {
            throw new NotFoundException("Tour or User not found");
        }
        if(discountPercent < 0 || discountPercent > 100) {
            throw new BadRequestException("Invalid discount percent");
        }
        if(!tour.get().getAgency().getId().equals(agencyId)) {
            throw new NotFoundException("Agency not found");
        }

        tour.get().setDiscountPercent(discountPercent);
        tour.get().setPriceWithDiscount(
                tour.get().getPrice().multiply(
                        BigDecimal.valueOf(100f - discountPercent)
                ).divide(BigDecimal.valueOf(100f))
        );
        tourRepository.save(tour.get());
        return toResponseDto(tour.orElse(null));

    }

    private TourResponseDto toResponseDto(Tour tour) {
        Optional<User> agency = userRepository.findById(tour.getAgency().getId());

        if(agency.isEmpty()) {
            throw new NotFoundException("Agency not found");
        }
        return TourResponseDto.builder()
                .id(tour.getId())
                .agencyName(agency.get().getFullName())
                .agencyId(agency.get().getId())
                .title(tour.getTitle())
                .description(tour.getDescription())
                .nights(calculatingNights(tour.getStartDate(), tour.getReturnDate()))
                .startDate(tour.getStartDate())
                .returnDate(tour.getReturnDate())
                .price(tour.getPrice())
                .priceWithDiscount(tour.getPriceWithDiscount())
                .hotel(tour.getHotel())
                .city(tour.getCity())
                .seatsTotal(tour.getSeatsTotal())
                .seatsAvailable(tour.getSeatsAvailable())
                .views(tour.getViews())
                .rating(tour.getRating())
                .discountPercent(tour.getDiscountPercent())
                .build();
    }

    private Integer calculatingNights(LocalDate startDate, LocalDate returnDate) {
        if (returnDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Return date must not be before start date!");
        }

        int nights = (int) ChronoUnit.DAYS.between(startDate, returnDate);

        if (nights <= 0) {
            throw new IllegalArgumentException("Tour must have at least one night!");
        }
        return nights;
    }

}
