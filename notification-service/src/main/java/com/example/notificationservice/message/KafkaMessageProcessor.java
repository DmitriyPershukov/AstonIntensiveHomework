package com.example.notificationservice.message;

import com.example.notificationservice.notification.NotificationSender;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class KafkaMessageProcessor implements MessageProcessor{
    private NotificationSender notificationSender;

    public KafkaMessageProcessor(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

    @Override
    public void process(String message) {
       String event = message.split(" ")[0];
       String email = message.split(" ")[1];
       try{
           switch (event){
               case "created":
                   notificationSender.sendCreatedNotification(email);
                   break;
               case "deleted":
                   notificationSender.sendDeletedNotification(email);
                   break;
           }
       } catch (IOException ex){

       }

    }
}
