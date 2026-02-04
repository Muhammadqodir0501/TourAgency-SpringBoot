package org.example.touragency.service.abstractions;

import org.example.touragency.dto.request.TourAddDto;
import org.example.touragency.dto.response.TourResponseDto;
import org.example.touragency.dto.response.TourUpdateDto;
import org.example.touragency.model.entity.Tour;

import java.util.List;
import java.util.UUID;

public interface TourService {
    TourResponseDto addNewTour(UUID agencyId, TourAddDto tourAddDto);

    void deleteTour(UUID agencyId, UUID tourId);

    TourResponseDto updateTour(UUID agencyId, UUID tourId, TourUpdateDto tourUpdateDto);

    List<TourResponseDto> getAllTours();

    TourResponseDto getTourById(UUID agencyId, UUID tourId);

    List<TourResponseDto> getAllToursByAgencyId(UUID agencyId);

    void tourIsBooked(Tour tour);

    void tourBookingIsCanceled(UUID tourId);

    TourResponseDto addDiscount(UUID agencyId, UUID tourId, Integer discountPercent);
}
