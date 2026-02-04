package org.example.touragency.controller;

import lombok.RequiredArgsConstructor;
import org.example.touragency.dto.response.BookingResponseDto;
import org.example.touragency.exception.ApiResponse;
import org.example.touragency.service.abstractions.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bookings/{userId}")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/{tourId}")
    public ResponseEntity<ApiResponse<BookingResponseDto>> addBooking(
            @PathVariable UUID userId,
            @PathVariable UUID tourId) {
        BookingResponseDto booking = bookingService.addBooking(userId, tourId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(booking));
    }

    @DeleteMapping("/{tourId}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable UUID userId,
            @PathVariable UUID tourId
    ) {
        bookingService.cancelBooking(userId, tourId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponseDto>>> getBookings(@PathVariable UUID userId) {
        List<BookingResponseDto> bookings = bookingService.getUsersBookings(userId);
        return ResponseEntity.ok(new ApiResponse<>(bookings));
    }
}

