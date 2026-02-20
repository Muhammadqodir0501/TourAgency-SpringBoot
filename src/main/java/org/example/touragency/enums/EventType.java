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

    USER_REGISTERED("user.auth.registered"),
    USER_NOT_FOUND("user.error.not_found"),
    LOGIN_FAILED("user.auth.login_failed");

    private final String routingKey;

    EventType(String routingKey) {
        this.routingKey = routingKey;
    }
}
