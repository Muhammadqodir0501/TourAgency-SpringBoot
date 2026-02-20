package org.example.touragency.controller;

import lombok.RequiredArgsConstructor;
import org.example.touragency.dto.response.FavTourResponseDto;
import org.example.touragency.security.SecurityUtils;
import org.example.touragency.service.abstractions.FavouriteTourService;
import org.example.touragency.exception.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/favourites")
@RequiredArgsConstructor
public class FavouriteTourController {

    private final FavouriteTourService favouriteTourService;

    @PostMapping("/{tourId}")
    public ResponseEntity<ApiResponse<FavTourResponseDto>> addFavTour(@PathVariable UUID tourId) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        FavTourResponseDto favouriteTour = favouriteTourService.addFavouriteTour(currentUserId, tourId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(favouriteTour));
    }

    @DeleteMapping("/{tourId}")
    public ResponseEntity<ApiResponse<Void>> deleteFavouriteTour(@PathVariable UUID tourId) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        favouriteTourService.deleteFavouriteTour(currentUserId,tourId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<FavTourResponseDto>>> getUserFavouriteTour(){
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        List<FavTourResponseDto> tours = favouriteTourService.getUserFavouriteTours(currentUserId);
       return ResponseEntity.ok(new ApiResponse<>(tours));
    }
}
