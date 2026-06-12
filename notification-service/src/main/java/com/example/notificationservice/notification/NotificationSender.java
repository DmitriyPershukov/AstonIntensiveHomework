package com.example.notificationservice.notification;

public interface NotificationSender {
    void sendCreatedNotification(String email);

    void sendDeletedNotification(String email);
}
