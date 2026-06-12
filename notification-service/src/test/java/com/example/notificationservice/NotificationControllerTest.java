package com.example.notificationservice;

import com.example.notificationservice.controller.NotificationController;
import com.example.notificationservice.notification.NotificationSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
}
