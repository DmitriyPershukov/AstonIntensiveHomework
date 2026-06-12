package com.example.notificationservice;

import com.example.notificationservice.notification.MailNotificationSender;
import com.example.notificationservice.notification.NotificationSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class MailNotificationSenderTest {
    private static final String expectedNotificationSubject = "Изменение статуса аккаунта.";
    private static final String expectedCreatedNotification =
            "Здравствуйте! Ваш аккаунт для user-service был успешно создан.";
    private static final String expectedDeletedNotification= "Здравствуйте! Ваш аккаунт был удалён.";
    @Mock
    private JavaMailSender mailSender;
    private NotificationSender mailNotificationSender;
    private MockitoSession mockitoSession;

    @BeforeEach
    void setUp(){
        mockitoSession = Mockito.mockitoSession()
                .initMocks(this)
                .startMocking();
        mailNotificationSender = new MailNotificationSender(mailSender);
    }

    @AfterEach
    void tearDown(){
        mockitoSession.finishMocking();
    }

    @Test
    void testSendNotificationSendCorrectEmailUponCreationMessage(){
        String emailAddress = "bill@gmail.com";
        SimpleMailMessage expectedMailMessage = new SimpleMailMessage();
        expectedMailMessage.setTo(emailAddress);
        expectedMailMessage.setSubject(expectedNotificationSubject);
        expectedMailMessage.setText(expectedCreatedNotification);
        mailNotificationSender.sendCreatedNotification(emailAddress);
        verify(mailSender).send(eq(expectedMailMessage));
    }

    @Test
    void testSendNotificationSendCorrectEmailUponDeletionMessage(){
        String emailAddress = "bill@gmail.com";
        SimpleMailMessage expectedMailMessage = new SimpleMailMessage();
        expectedMailMessage.setTo(emailAddress);
        expectedMailMessage.setSubject(expectedNotificationSubject);
        expectedMailMessage.setText(expectedDeletedNotification);
        mailNotificationSender.sendDeletedNotification(emailAddress);
        verify(mailSender).send(eq(expectedMailMessage));
    }
}
