package com.example.notificationservice.notification;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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

    private void sendNotification(String email, String notificationText){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(notificationSubject);
        mailMessage.setText(notificationText);
        emailSender.send(mailMessage);
    }

    @Override
    public void sendCreatedNotification(String email) {
        sendNotification(email, createdNotification);
    }

    @Override
    public void sendDeletedNotification(String email) {
        sendNotification(email, deletedNotification);
    }


}
