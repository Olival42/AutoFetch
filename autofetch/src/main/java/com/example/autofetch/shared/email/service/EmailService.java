package com.example.autofetch.shared.email.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Async
    public void sendResetPasswordEmail(String to, String resetLink) {

        Context context = new Context();
        context.setVariable("resetLink", resetLink);

        String html = templateEngine.process("email/reset-password.html", context);

        sendHtmlEmail(to, "Password Reset Request", html);
    }

    private void sendHtmlEmail(String to, String subject, String html) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }
}
