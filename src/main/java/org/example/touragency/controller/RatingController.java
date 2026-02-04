package org.example.touragency.controller;

import lombok.RequiredArgsConstructor;
import org.example.touragency.dto.request.RatingDto;
import org.example.touragency.dto.response.RatingResponseDto;
import org.example.touragency.service.abstractions.RatingService;
import org.example.touragency.exception.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<ApiResponse<RatingResponseDto>> giveRateToTour(@RequestBody RatingDto ratingDto) {
        RatingResponseDto rating =  ratingService.addRating(ratingDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(rating));
    }
}