package com.example.userservice.message.kafka;

import com.example.userservice.message.MessageProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageProducer implements MessageProducer {
    @Value(value = "${spring.kafka.topic}")
    private String topic;

    private KafkaTemplate<String, String> kafkaTemplate;

    public KafkaMessageProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(String message) {
        kafkaTemplate.send(topic, message);
    }
}
