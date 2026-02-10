package org.example.touragency.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.touragency.dto.response.FavTourResponseDto;
import org.example.touragency.exception.ConflictException;
import org.example.touragency.exception.NotFoundException;
import org.example.touragency.model.entity.FavouriteTour;
import org.example.touragency.model.entity.Tour;
import org.example.touragency.model.entity.User;
import org.example.touragency.repository.FavTourRepository;
import org.example.touragency.repository.TourRepository;
import org.example.touragency.repository.UserRepository;
import org.example.touragency.service.abstractions.FavouriteTourService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class FavouriteTourServiceImpl implements FavouriteTourService {

    private final FavTourRepository favTourRepository;
    private final TourRepository tourRepository;
    private  final UserRepository userRepository;


    @Override
    @Transactional
    public FavTourResponseDto addFavouriteTour(UUID  userId, UUID tourId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new NotFoundException("Tour not found"));

        boolean alreadyLiked =
                favTourRepository.findByUserIdAndTourId(userId, tourId).isPresent();

        if (alreadyLiked) {
            throw new ConflictException("User already added this tour to the favourite list");
        }

        FavouriteTour favouriteTour = FavouriteTour.builder()
                .tour(tour)
                .user(user)
                .build();

        favTourRepository.save(favouriteTour);

        return new FavTourResponseDto(
                favouriteTour.getId(),
                user.getId(),
                tour.getId()
        );
    }

    @Override
    @Transactional
    public void deleteFavouriteTour(UUID userId, UUID tourId) {

        tourRepository.findById(tourId)
                        .orElseThrow(() -> new NotFoundException("Tour not found"));

        userRepository.findById(userId)
                        .orElseThrow(() -> new NotFoundException("Agency not found"));

        favTourRepository.deleteByUserIdAndTourId(userId,tourId);
    }

    @Override
    public List<FavTourResponseDto> getUserFavouriteTours(UUID userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<FavouriteTour> favouriteTours = favTourRepository.findByUserId(userId);

        return favouriteTours.stream()
                .map(f -> new FavTourResponseDto(
                        f.getId(),
                        f.getUser().getId(),
                        f.getTour().getId()
                ))
                .toList();
    }
}
