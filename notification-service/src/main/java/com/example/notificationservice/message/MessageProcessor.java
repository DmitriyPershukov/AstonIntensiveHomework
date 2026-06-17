package com.example.notificationservice.message;

import java.io.IOException;

public interface MessageProcessor {
    void process(String message) throws IOException;
}
