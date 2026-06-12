package com.example.notificationservice.controller;

import com.example.notificationservice.notification.NotificationSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
public class NotificationController {
    private NotificationSender notificationSender;

    public NotificationController(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

    @PostMapping
    public void sendNotification(@RequestParam String event,
                                 @RequestParam String email){
        switch (event){
            case "created":
                notificationSender.sendCreatedNotification(email);
                break;
            case "deleted":
                notificationSender.sendDeletedNotification(email);
                break;
        }
    }
}
