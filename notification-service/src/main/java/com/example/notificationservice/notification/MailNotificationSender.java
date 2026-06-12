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

    public void sendNotification(String message){
        String event = message.split(" ")[0];
        String to_address = message.split(" ")[1];
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        //mailMessage.setFrom("noreply@notificationservice.com");
        mailMessage.setTo(to_address);
        mailMessage.setSubject(notificationSubject);
        if(event.equals("created")){
            mailMessage.setText(createdNotification);
        } else {
            mailMessage.setText(deletedNotification);
        }
        emailSender.send(mailMessage);
    }
}
