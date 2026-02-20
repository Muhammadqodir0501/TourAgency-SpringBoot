package org.example.touragency.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.touragency.dto.request.RatingDto;
import org.example.touragency.dto.response.RatingResponseDto;
import org.example.touragency.dto.response.TourResponseDto;
import org.example.touragency.enums.EventType;
import org.example.touragency.exception.NotFoundException;
import org.example.touragency.model.entity.Rating;
import org.example.touragency.model.entity.RatingCounter;
import org.example.touragency.model.entity.Tour;
import org.example.touragency.model.entity.User;
import org.example.touragency.repository.RatingCounterRepository;
import org.example.touragency.repository.RatingRepository;
import org.example.touragency.repository.TourRepository;
import org.example.touragency.repository.UserRepository;
import org.example.touragency.service.abstractions.OutboxService;
import org.example.touragency.service.abstractions.RatingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final RatingCounterRepository ratingCounterRepository;
    private final TourRepository tourRepository;
    private final UserRepository userRepository;
    private final OutboxService outboxService;

    @Override
    public RatingResponseDto addRating(RatingDto ratingDto) {

        UUID tourId = ratingDto.getTourId();
        UUID userId = ratingDto.getUserId();

        Rating existRating = ratingRepository
                .findByUserIdAndTourId(userId, tourId)
                .orElse(null);

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new NotFoundException("Tour not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (existRating != null) {
            updateExistRating(ratingDto, existRating);
            syncTourRatingFromCounter(tourId);
            return toResponseDto(existRating);
        }

        Rating rating = Rating.builder()
                .tourId(tour.getId())
                .userId(user.getId())
                .rate(ratingDto.getRate())
                .build();

        ratingRepository.save(rating);
        ratingCount(ratingDto);
        syncTourRatingFromCounter(tourId);

        RatingResponseDto ratingResDto = toResponseDto(rating);

        outboxService.createAndSaveOutboxEvent(
                EventType.TOUR_RATED,
                String.valueOf(rating.getId()),
                userId,
                ratingResDto
        );

        return ratingResDto;
    }


    @Override
    public void ratingCount(RatingDto ratingDto) {
        UUID tourId = ratingDto.getTourId();

        Optional<RatingCounter> optionalCounter =
                ratingCounterRepository.findByTourId(tourId);

        if (optionalCounter.isPresent()) {
            RatingCounter counter = optionalCounter.get();

            float newAvg =
                    (counter.getAverageRating() * counter.getRatingAmount()
                            + ratingDto.getRate())
                            / (counter.getRatingAmount() + 1);

            counter.setAverageRating(newAvg);
            counter.setRatingAmount(counter.getRatingAmount() + 1);

            ratingCounterRepository.save(counter);
        } else {
            RatingCounter counter = RatingCounter.builder()
                    .tourId(tourId)
                    .averageRating(ratingDto.getRate())
                    .ratingAmount(1)
                    .build();

            ratingCounterRepository.save(counter);
        }
    }


    @Override
    public void updateExistRating(RatingDto ratingDto, Rating existRating) {
        Optional<RatingCounter> counter = ratingCounterRepository.findByTourId(existRating.getTourId());

        if(counter.isPresent()){
            float newAvg = (counter.get().getAverageRating() * counter.get().getRatingAmount()
                    - existRating.getRate() + ratingDto.getRate())
                    / counter.get().getRatingAmount();

            counter.get().setAverageRating(newAvg);
            ratingCounterRepository.save(counter.get());

            existRating.setRate(ratingDto.getRate());
            ratingRepository.save(existRating);

            RatingResponseDto ratingResDto = toResponseDto(existRating);

            outboxService.createAndSaveOutboxEvent(
                    EventType.USER_RATING_UPDATED,
                    String.valueOf(existRating.getId()),
                    ratingDto.getUserId(),
                    ratingResDto
            );

        }
    }


    private void syncTourRatingFromCounter(UUID tourId) {
        Tour tour = tourRepository.findById(tourId).orElse(null);
        RatingCounter counter = ratingCounterRepository.findByTourId(tourId).orElse(null);

        if (tour == null) return;

        if (counter != null) {
            tour.setRating(counter.getAverageRating());
        } else {
            tour.setRating(0);
        }

        tourRepository.save(tour);
    }

    private RatingResponseDto toResponseDto(Rating rating) {
        return RatingResponseDto.builder()
                .ratingId(rating.getId())
                .tourId(rating.getTourId())
                .userId(rating.getUserId())
                .userRating(rating.getRate())
                .build();
    }


}