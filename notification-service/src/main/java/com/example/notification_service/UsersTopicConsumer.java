package com.example.notification_service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UsersTopicConsumer {
    @KafkaListener(topics = "users", groupId = "users.email-notification")
    public void listenUsers(String message) {
        System.out.println(message);
    }
}
