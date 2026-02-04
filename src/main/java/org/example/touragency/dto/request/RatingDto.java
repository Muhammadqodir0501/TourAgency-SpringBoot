package org.example.touragency.dto.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingDto {
    private UUID tourId;
    private UUID userId;
    private float rate;
}