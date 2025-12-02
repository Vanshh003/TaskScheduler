package com.pers.taskScheduler.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pers.taskScheduler.dto.EmailPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailTaskHandler {

    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void executeEmail(String payloadJson) throws Exception {

        if (payloadJson == null || payloadJson.isBlank()) {
            throw new RuntimeException("Email payload is missing");
        }

        EmailPayload payload = objectMapper.readValue(payloadJson, EmailPayload.class);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(payload.getTo());
        msg.setSubject(payload.getSubject());
        msg.setText(payload.getBody());

        mailSender.send(msg);

        log.info("Email successfully sent to {}", payload.getTo());
    }
}
