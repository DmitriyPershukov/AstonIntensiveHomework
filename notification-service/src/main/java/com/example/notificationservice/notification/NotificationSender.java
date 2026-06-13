package com.example.notificationservice.notification;

import java.io.IOException;

public interface NotificationSender {
    void sendCreatedNotification(String email) throws IOException;

    void sendDeletedNotification(String email) throws IOException;
}
