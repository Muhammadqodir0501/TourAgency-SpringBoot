package org.example.touragency.service.abstractions;

import org.example.touragency.enums.EventType;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface OutboxService {

    @Transactional
    void createAndSaveOutboxEvent(EventType eventType, String entityId, UUID userId, Object payload);
}
