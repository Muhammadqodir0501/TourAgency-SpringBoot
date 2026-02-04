package org.example.touragency.service.abstractions;

import org.example.touragency.dto.response.FavTourResponseDto;
import java.util.List;
import java.util.UUID;

public interface FavouriteTourService {
    FavTourResponseDto addFavouriteTour(UUID userId, UUID tourId);

    void deleteFavouriteTour(UUID userId, UUID tourId);

    List<FavTourResponseDto> getUserFavouriteTours(UUID userId);
}
