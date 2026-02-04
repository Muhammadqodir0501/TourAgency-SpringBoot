package org.example.touragency.controller;

import lombok.RequiredArgsConstructor;
import org.example.touragency.dto.response.FavTourResponseDto;
import org.example.touragency.service.abstractions.FavouriteTourService;
import org.example.touragency.exception.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/{userId}/favourites")
@RequiredArgsConstructor
public class FavouriteTourController {

    private final FavouriteTourService favouriteTourService;

    @PostMapping("/{tourId}")
    public ResponseEntity<ApiResponse<FavTourResponseDto>> addFavTour(@PathVariable UUID userId, @PathVariable UUID tourId) {
        FavTourResponseDto favouriteTour = favouriteTourService.addFavouriteTour(userId, tourId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(favouriteTour));
    }

    @DeleteMapping("/{tourId}")
    public ResponseEntity<ApiResponse<Void>> deleteFavouriteTour(@PathVariable UUID userId, @PathVariable UUID tourId) {
        favouriteTourService.deleteFavouriteTour(userId,tourId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<FavTourResponseDto>>> getUserFavouriteTour(@PathVariable UUID userId){
        List<FavTourResponseDto> tours = favouriteTourService.getUserFavouriteTours(userId);
       return ResponseEntity.ok(new ApiResponse<>(tours));
    }
}
