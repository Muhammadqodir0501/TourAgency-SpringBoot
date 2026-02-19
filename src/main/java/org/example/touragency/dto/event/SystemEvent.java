package org.example.touragency.dto.event;

import lombok.Builder;
import lombok.Data;
import org.example.touragency.enums.EventType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SystemEvent {

    private EventType eventType;
    private UUID userId;
    private String entityId;
    private LocalDateTime timestamp;
    private Object payload;
}