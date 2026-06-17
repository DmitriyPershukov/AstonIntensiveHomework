package com.example.notificationservice.notification;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MailNotificationSender implements NotificationSender{
    private static final String notificationSubject = "Изменение статуса аккаунта.";
    private static final String createdNotification =
            "Здравствуйте! Ваш аккаунт для user-service был успешно создан.";
    private static final String deletedNotification= "Здравствуйте! Ваш аккаунт был удалён.";

    private JavaMailSender emailSender;

    public MailNotificationSender(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    private void sendNotification(String email, String notificationText) throws IOException {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(notificationSubject);
        mailMessage.setText(notificationText);
        try{
            emailSender.send(mailMessage);
        } catch (MailException ex){
            throw new IOException("Service failed to send notification mail.");
        }
    }

    @Override
    public void sendCreatedNotification(String email) throws IOException {
        sendNotification(email, createdNotification);
    }

    @Override
    public void sendDeletedNotification(String email) throws IOException {
        sendNotification(email, deletedNotification);
    }
}
