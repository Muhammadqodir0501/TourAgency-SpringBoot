package org.example.touragency.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class BookingResponseDto {
    private UUID bookingId;
    private UUID userId;
    private UUID tourId;
}
