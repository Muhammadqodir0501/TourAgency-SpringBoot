package org.example.touragency.controller;

import lombok.RequiredArgsConstructor;
import org.example.touragency.dto.request.TourAddDto;
import org.example.touragency.dto.response.TourResponseDto;
import org.example.touragency.dto.response.TourUpdateDto;
import org.example.touragency.security.SecurityUtils;
import org.example.touragency.service.abstractions.TourService;
import org.example.touragency.exception.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/agencies/{agencyId}/tours")
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;

    @PostMapping()
    public ResponseEntity<ApiResponse<TourResponseDto>> addNewTour(@RequestBody TourAddDto tourAddDto) {
        TourResponseDto createdTour = tourService.addNewTour(tourAddDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(createdTour));

    }

    @DeleteMapping("/{tourId}")
    public ResponseEntity<ApiResponse<Void>> deleteTour(@PathVariable UUID tourId) {
        UUID currentAgencyId = SecurityUtils.getCurrentUserId();
        tourService.deleteTour(currentAgencyId,tourId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{tourId}")
    public ResponseEntity<ApiResponse<TourResponseDto>> updateTour(
                                        @PathVariable UUID tourId,
                                        @RequestBody TourUpdateDto tourUpdateDto) {
        UUID currentAgencyId = SecurityUtils.getCurrentUserId();
        TourResponseDto updatedTour = tourService.updateTour(currentAgencyId,tourId,tourUpdateDto);
        return ResponseEntity.ok(new ApiResponse<>(updatedTour));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<TourResponseDto>>> getAllTours() {
        List<TourResponseDto> tours = tourService.getAllTours();
        return ResponseEntity.ok(new ApiResponse<>(tours));
    }

    @GetMapping("/agency")
    public ResponseEntity<ApiResponse<List<TourResponseDto>>> getAllToursByAgency(@PathVariable UUID agencyId) {
        List<TourResponseDto> tours = tourService.getAllToursByAgencyId(agencyId);
        return ResponseEntity.ok(new ApiResponse<>(tours));
    }

    @GetMapping("/{tourId}")
    public ResponseEntity<ApiResponse<TourResponseDto>>  getTourById(@PathVariable UUID tourId) {
        TourResponseDto tour = tourService.getTourById(tourId);
        return ResponseEntity.ok(new ApiResponse<>(tour));
    }

    @PostMapping("/{tourId}")
    public ResponseEntity<ApiResponse<TourResponseDto>> addDiscount
            (@PathVariable UUID tourId, @RequestBody Integer discountPercent ) {
        UUID currentAgencyId = SecurityUtils.getCurrentUserId();
        TourResponseDto tour = tourService.addDiscount(currentAgencyId,tourId,discountPercent);
        return ResponseEntity.ok(new ApiResponse<>(tour));
    }


}
