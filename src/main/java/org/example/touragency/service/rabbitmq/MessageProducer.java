package org.example.touragency.service.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.touragency.config.rabbit.RabbitConfig;
import org.example.touragency.dto.event.SystemEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendEvent(SystemEvent event) {
        try {
            String routingKey = event.getEventType().getRoutingKey();

            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE_NAME,
                    routingKey,
                    event
            );
            log.info("Event has succesfully sent: {} [Key: {} ]", event.getEventType(),routingKey);
        } catch (Exception e) {
            log.error("Error while sending event to RabbitMQ", e);
        }
    }
}