package org.example.touragency.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.touragency.dto.event.SystemEvent;
import org.example.touragency.enums.EventStatus;
import org.example.touragency.enums.EventType;
import org.example.touragency.model.entity.OutboxEvent;
import org.example.touragency.repository.OutboxEventRepository;
import org.example.touragency.service.abstractions.OutboxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxServiceImpl implements OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @Override
    public void createAndSaveOutboxEvent(EventType eventType, String entityId, UUID userId, Object payload){
        try {
            SystemEvent systemEvent = SystemEvent.builder()
                    .eventType(eventType)
                    .entityId(entityId)
                    .userId(userId)
                    .timestamp(LocalDateTime.now())
                    .payload(payload)
                    .build();

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .eventType(eventType)
                    .status(EventStatus.PENDING)
                    .payload(objectMapper.writeValueAsString(systemEvent))
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxEventRepository.save(outboxEvent);
            log.debug("Saved Outbox event: {} for entity: {}", eventType, entityId);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize Outbox event payload for entity: {}", entityId, e);
            throw new RuntimeException("Could not serialize event payload", e);
        }
    }
}
