package com.example.notificationservice.kafka;

import com.example.notificationservice.notification.NotificationSender;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UsersTopicConsumer {
    private NotificationSender notificationSender;

    public UsersTopicConsumer(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

    @KafkaListener(topics = "users", groupId = "users.email-notification")
    public void listenUsers(String message) {
        notificationSender.sendNotification(message);
    }
}
