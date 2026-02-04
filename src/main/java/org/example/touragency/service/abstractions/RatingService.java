package org.example.touragency.service.abstractions;

import org.example.touragency.dto.request.RatingDto;
import org.example.touragency.dto.response.RatingResponseDto;
import org.example.touragency.model.entity.Rating;

public interface RatingService {
    RatingResponseDto addRating(RatingDto ratingDto);
    void ratingCount(RatingDto ratingDto);
    void updateExistRating(RatingDto ratingDto, Rating existingRating);

}