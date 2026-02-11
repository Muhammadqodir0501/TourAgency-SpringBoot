package org.example.touragency.service.listener;

import lombok.extern.slf4j.Slf4j;
import org.example.touragency.config.rabbit.RabbitConfig;
import org.example.touragency.dto.event.TourCreatedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationListener {

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void handleTourCreated(TourCreatedEvent event){
        log.info(" [Notification Service] Received event: {}", event);
        log.info(" Sending Notification for the tour '{}' to city '{}'", event.getTourTitle(), event.getCity());

    }
}
