package com.example.notificationservice.kafka;

import com.example.notificationservice.message.MessageProcessor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UsersTopicConsumer {
    private MessageProcessor messageProcessor;

    public UsersTopicConsumer(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @KafkaListener(topics = "users", groupId = "users.email-notification")
    public void listenUsers(String message) {
        messageProcessor.process(message);
    }
}
