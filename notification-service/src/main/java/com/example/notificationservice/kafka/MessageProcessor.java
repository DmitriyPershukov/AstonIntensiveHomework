package com.example.notificationservice.kafka;

public interface MessageProcessor {
    void process(String message);
}
