package org.example.touragency.service.abstractions;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

public interface OutboxScheduler {
    @Scheduled(fixedDelay = 5000)
    @Transactional
    void processOutboxEvent();
}
