package org.example.touragency.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FavTourResponseDto {
    private UUID favouriteTourId;
    private UUID userId;
    private UUID tourId;
}
