package com.example.notificationservice;

import com.example.notificationservice.controller.NotificationController;
import com.example.notificationservice.notification.NotificationSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.stream.Stream;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationSender notificationSender;

    @Test
    void testSendNotificationSendsCorrectMailWhenCreatedNotificationRequested() throws Exception {
        String emailAddress = "test@email.com";
        mockMvc.perform(post("/notification")
                        .param("event", "created")
                        .param("email", emailAddress)
        ).andExpect(status().isOk());
        verify(notificationSender).sendCreatedNotification(eq(emailAddress));
    }

    @Test
    void testSendNotificationSendsCorrectMailWhenDeletedNotificationRequested() throws Exception {
        String emailAddress = "test@email.com";
        mockMvc.perform(post("/notification")
                .param("event", "deleted")
                .param("email", emailAddress)
        ).andExpect(status().isOk());
        verify(notificationSender).sendDeletedNotification(eq(emailAddress));
    }

    @ParameterizedTest
    @MethodSource("supplyInvalidParameters")
    void testSendNotificationReturnsBadRequestHttpStatusWhenParametersAreNotValid(
            String event,
            String email
    ) throws Exception {
        mockMvc.perform(post("/notification")
                .param("event", event)
                .param("email", email)
        ).andExpect(status().isBadRequest());
    }

    static Stream<Arguments> supplyInvalidParameters(){
        return Stream.of(
                Arguments.of("updated", "test@email.com"),
                Arguments.of("created", "test")
        );
    }

    @Test
    void testSendNotificationReturnsInternalServerErrorHttpStatusWhenNotificationSenderThrowsException()
            throws Exception {
        String emailAddress = "test@email.com";
        doThrow(IOException.class).when(notificationSender).sendCreatedNotification(emailAddress);
        mockMvc.perform(post("/notification")
                .param("event", "created")
                .param("email", emailAddress)
        ).andExpect(status().isInternalServerError());
    }
}
