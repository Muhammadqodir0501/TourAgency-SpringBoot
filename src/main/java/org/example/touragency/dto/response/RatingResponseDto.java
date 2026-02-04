package org.example.touragency.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponseDto {
    private UUID ratingId;
    private UUID tourId;
    private UUID userId;
    private float userRating;
    private float averageRating;
    private double ratingCount;
}