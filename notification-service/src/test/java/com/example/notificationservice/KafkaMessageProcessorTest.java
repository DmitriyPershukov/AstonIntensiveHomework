package com.example.notificationservice;

import com.example.notificationservice.message.kafka.KafkaMessageProcessor;
import com.example.notificationservice.notification.NotificationSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class KafkaMessageProcessorTest {
    @Mock
    private NotificationSender notificationSender;
    private KafkaMessageProcessor kafkaMessageProcessor;
    private MockitoSession mockitoSession;

    @BeforeEach
    void setUp(){
        mockitoSession = Mockito.mockitoSession()
                .initMocks(this)
                .startMocking();
        kafkaMessageProcessor = new KafkaMessageProcessor(notificationSender);
    }

    @AfterEach
    void tearDown(){
        mockitoSession.finishMocking();
    }

    @Test
    void testProcessCallsCorrectMethodUponCreatedMessage() throws IOException {
        String email = "test@email.com";
        String message = String.format("created %s", email);
        kafkaMessageProcessor.process(message);
        verify(notificationSender).sendCreatedNotification(eq(email));
    }

    @Test
    void testProcessCallsCorrectMethodUponDeletedMessage() throws IOException {
        String email = "test@email.com";
        String message = String.format("deleted %s", email);
        kafkaMessageProcessor.process(message);
        verify(notificationSender).sendDeletedNotification(eq(email));
    }
}
