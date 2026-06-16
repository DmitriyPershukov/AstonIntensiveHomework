package com.example.notificationservice.kafka;

import com.example.notificationservice.message.MessageProcessor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UsersTopicConsumer {
    private MessageProcessor messageProcessor;

    public UsersTopicConsumer(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.group-id}")
    public void listenUsers(String message) throws IOException {
        messageProcessor.process(message);
    }
}
