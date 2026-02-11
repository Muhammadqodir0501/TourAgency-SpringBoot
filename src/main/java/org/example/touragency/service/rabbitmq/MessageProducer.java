package org.example.touragency.service.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.example.touragency.config.rabbit.RabbitConfig;
import org.example.touragency.dto.event.TourCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendTourCreatedMessage(TourCreatedEvent event) {
        System.out.println("[RabbitMQ] Sending Message: " + event.getTourTitle());

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY,
                event
        );
    }
}