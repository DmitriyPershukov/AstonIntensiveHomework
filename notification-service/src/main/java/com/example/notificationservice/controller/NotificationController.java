package com.example.notificationservice.controller;

import com.example.notificationservice.notification.NotificationSender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/notification")
@Validated
public class NotificationController {
    private NotificationSender notificationSender;

    public NotificationController(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

    @PostMapping
    public void sendNotification(
            @Pattern(regexp = "created|deleted", message = "Event must be either created or deleted.")
            @RequestParam
            String event,
            @Email(message = "Email parameter should contain valid email address.")
            @RequestParam
            String email) throws IOException {
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
