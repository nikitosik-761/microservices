package org.notification.services;

import lombok.RequiredArgsConstructor;
import org.clients.notification.NotificationRequest;
import org.notification.models.Notification;
import org.notification.repositories.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void send(NotificationRequest notificationRequest){
        notificationRepository.save(
                Notification.builder()
                        .toCustomerId(notificationRequest.toCustomerId())
                        .toCustomerEmail(notificationRequest.toCustomerEmail())
                        .sender("Nikitosik_761")
                        .message(notificationRequest.message())
                        .sendAt(LocalDateTime.now())
                        .build()
        );
    }

}
