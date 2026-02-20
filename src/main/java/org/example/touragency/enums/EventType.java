package org.example.touragency.enums;

import lombok.Getter;

@Getter
public enum EventType {
    TOUR_CREATED("tour.booking.created"),
    TOUR_UPDATED("tour.booking.updated"),
    TOUR_DELETED("tour.booking.deleted"),
    TOUR_BOOKED("tour.booking.booked"),
    TOUR_CANCELLED("tour.booking.cancelled"),
    TOUR_NOT_FOUND("tour.error.not_found"),
    TOUR_ADDED_DISCOUNT("tour.booking.added.discount"),
    TOUR_LIKED("tour.booking.liked"),
    TOUR_UNLIKED("tour.booking.unliked"),

    TOUR_RATED("tour.booking.rated"),
    USER_RATING_UPDATED("user.booking.rating_updated"),

    USER_REGISTERED("user.auth.registered"),
    AGENCY_CREATED("agency.booking.created"),
    AGENCY_DELETED("agency.booking.deleted"),
    USER_DELETED("user.booking.deleted"),
    AGENCY_UPDATED("agency.booking.updated"),
    USER_UPDATED("user.booking.updated");

    private final String routingKey;

    EventType(String routingKey) {
        this.routingKey = routingKey;
    }
}
