package be.pxl.services.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    @RabbitListener(queues = "post-status-queue")
    public void listen(String message) {
        log.info("Message received: {}", message);
    }
}
