package org.example.touragency.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.touragency.dto.event.SystemEvent;
import org.example.touragency.enums.EventStatus;
import org.example.touragency.model.entity.OutboxEvent;
import org.example.touragency.repository.OutboxEventRepository;
import org.example.touragency.service.abstractions.OutboxScheduler;
import org.example.touragency.service.rabbitmq.MessageProducer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxSchedulerImpl implements OutboxScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final MessageProducer messageProducer;
    private final ObjectMapper objectMapper;


    @Scheduled(fixedDelay = 5000)
    @Transactional
    @Override
    public void processOutboxEvent() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findAllByStatusOrderByCreatedAtAsc(EventStatus.PENDING);

        if(pendingEvents.isEmpty()) {
            return;
        }

        log.info("Found {} PENDING events in Outbox. Sending to RabbitMQ...", pendingEvents.size());

        for(OutboxEvent event : pendingEvents) {
            try{
                SystemEvent systemEvent = objectMapper.readValue(event.getPayload(), SystemEvent.class);
                messageProducer.sendEvent(systemEvent);

                outboxEventRepository.delete(event);

                log.info("Event {} successfully sent to RabbitMQ", event.getId());
            } catch (Exception e){
                log.error("Error while sending event {} to RabbitMQ", event.getId(), e);
            }
        }
    }

}
