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
import org.example.touragency.service.abstractions.OutboxService;
import org.example.touragency.service.abstractions.RatingService;
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
    private final RatingCounterRepository ratingCounterRepository;
    private final FavTourRepository favTourRepository;
    private final BookingRepository bookingRepository;
    private final OutboxService outboxService;


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

        TourResponseDto tourDto = toResponseDto(newTour);

        outboxService.createAndSaveOutboxEvent(
               EventType.TOUR_CREATED,
               String.valueOf(newTour.getId()),
               agency.getId(),
               tourDto
       );

        return tourDto;

    }


    @PreAuthorize("hasRole('AGENCY')")
    @Override
    @Transactional
    public void deleteTour(UUID agencyId, UUID tourId) {

        userRepository.findById(agencyId)
                .orElseThrow(() -> new NotFoundException("Agency not found"));

        Tour existTour = tourRepository.findById(tourId)
                .orElseThrow(() -> new NotFoundException("Tour not found"));

        if (!existTour.getAgency().getId().equals(agencyId)) {
            throw new ForbiddenException("Tour's Agency does not belong to this Agency");
        }

        TourResponseDto payloadDto = toResponseDto(existTour);

        outboxService.createAndSaveOutboxEvent(
                EventType.TOUR_DELETED,
                String.valueOf(existTour.getId()),
                agencyId,
                payloadDto
        );

        ratingRepository.deleteByTourId(tourId);
        ratingCounterRepository.deleteByTourId(tourId);
        favTourRepository.deleteByTourId(tourId);
        bookingRepository.deleteByTourId(tourId);
        tourRepository.delete(existTour);

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
        existingTour.get().setStartDate(tourUpdateDto.getStartDate());
        existingTour.get().setReturnDate(tourUpdateDto.getReturnDate());
        existingTour.get().setNights(nights);
        existingTour.get().setHotel(tourUpdateDto.getHotel());
        tourRepository.save(existingTour.get());


        TourResponseDto updatedTourDto = toResponseDto(existingTour.get());

        outboxService.createAndSaveOutboxEvent(
                EventType.TOUR_UPDATED,
                String.valueOf(tourId),
                agencyId,
                updatedTourDto
        );

        return updatedTourDto;



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
        tourRepository.save(tour.get());
        return toResponseDto(tour.orElse(null));
    }

    @Override
    public List<TourResponseDto> getAllToursByAgencyId(UUID agencyId) {

        userRepository.findById(agencyId).orElseThrow(() -> new NotFoundException("Agency not found"));

        return tourRepository.findAllByAgencyId(agencyId).stream()
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

        TourResponseDto updatedTourDto = toResponseDto(tour.get());

        outboxService.createAndSaveOutboxEvent(
                EventType.TOUR_ADDED_DISCOUNT,
                String.valueOf(tourId),
                agencyId,
                updatedTourDto
        );

        return updatedTourDto;

    }

    private TourResponseDto toResponseDto(Tour tour) {
        User agency = tour.getAgency();

        if(agency == null) {
            throw new IllegalStateException("Database corruption: Tour " + tour.getId() + " has no assigned agency");
        }
        return TourResponseDto.builder()
                .id(tour.getId())
                .agencyName(agency.getFullName())
                .agencyId(agency.getId())
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
