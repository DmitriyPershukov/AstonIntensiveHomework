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
    private static final String EVENT_VALIDATION_MESSAGE = "Event must be either created or deleted.";
    private static final String EMAIL_VALIDATION_MESSAGE =
            "Email parameter should contain valid email address.";
    private NotificationSender notificationSender;

    public NotificationController(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

    @PostMapping
    public void sendNotification(
            @Pattern(regexp = "created|deleted", message = EVENT_VALIDATION_MESSAGE)
            @RequestParam
            String event,
            @Email(message = EMAIL_VALIDATION_MESSAGE)
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
