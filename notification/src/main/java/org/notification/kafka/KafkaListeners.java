package org.notification.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clients.notification.NotificationRequest;
import org.notification.models.Notification;
import org.notification.services.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class KafkaListeners {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "notific",
            groupId = "groupId"
    )
    void listener(NotificationRequest data){
        log.info("Consumed {} from queue KAFFFFKKAAAAA", data);
        notificationService.send(data);
    }
}
