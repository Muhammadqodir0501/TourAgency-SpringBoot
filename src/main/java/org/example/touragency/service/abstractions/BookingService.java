package org.example.touragency.service.abstractions;

import org.example.touragency.dto.response.BookingResponseDto;

import java.util.List;
import java.util.UUID;

public interface BookingService {

    BookingResponseDto addBooking(UUID userId, UUID tourId);

    List<BookingResponseDto> getUsersBookings(UUID userId);

    void cancelBooking(UUID userId, UUID tourId);
}
