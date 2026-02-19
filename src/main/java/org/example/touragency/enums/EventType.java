package org.example.touragency.enums;

import lombok.Getter;

@Getter
public enum EventType {
    TOUR_CREATED("tour.booking.created"),
    TOUR_CANCELLED("tour.booking.cancelled"),
    TOUR_NOT_FOUND("tour.error.not_found"),

    USER_REGISTERED("user.auth.registered"),
    USER_NOT_FOUND("user.error.not_found"),
    LOGIN_FAILED("user.auth.login_failed");

    private final String routingKey;

    EventType(String routingKey) {
        this.routingKey = routingKey;
    }
}
