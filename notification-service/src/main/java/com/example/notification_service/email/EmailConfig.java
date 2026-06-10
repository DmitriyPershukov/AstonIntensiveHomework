package com.example.notification_service.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {
    @Value(value = "${spring.mail.host}")
    private String emailHost;
    @Value(value = "${spring.mail.port}")
    private int emailPort;
    @Value(value = "${spring.mail.username}")
    private String emailUsername;
    @Value(value = "${spring.mail.password}")
    private String emailPassword;
    @Value(value = "${spring.mail.smtp.auth}")
    private String smtpAuth;
    @Value(value = "${spring.mail.smtp.starttls.enable}")
    private String tlsEnable;
    @Value(value = "${spring.mail.debug}")
    private String debug;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailHost);
        mailSender.setPort(emailPort);

        mailSender.setUsername(emailUsername);
        mailSender.setPassword(emailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.starttls.enable", tlsEnable);
        props.put("mail.debug", debug);

        return mailSender;
    }
}
