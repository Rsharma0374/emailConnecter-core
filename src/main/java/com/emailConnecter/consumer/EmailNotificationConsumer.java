package com.emailConnecter.consumer;

import com.emailConnecter.event.EmailEventPayload;
import com.emailConnecter.request.EmailRequest;
import com.emailConnecter.service.AwsSesEmailService;
import com.emailConnecter.service.EmailEventIdempotencyStore;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationConsumer.class);

    private final AwsSesEmailService awsSesEmailService;
    private final EmailEventIdempotencyStore idempotencyStore;
    private final Validator validator;

    @Autowired
    public EmailNotificationConsumer(
            AwsSesEmailService awsSesEmailService,
            EmailEventIdempotencyStore idempotencyStore,
            Validator validator) {
        this.awsSesEmailService = awsSesEmailService;
        this.idempotencyStore = idempotencyStore;
        this.validator = validator;
    }

    @KafkaListener(topics = "email-notifications", groupId = "${spring.kafka.consumer.group-id:email-connector-group}")
    public void consumeEmailNotification(EmailEventPayload payload) {
        var violations = validator.validate(payload);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        logger.info("Received email notification event. RequestId: {}, To: {}", 
                payload.getRequestId(), maskEmail(payload.getRecipientEmail()));
        if (!idempotencyStore.claim(payload.getRequestId())) {
            logger.warn("Skipping duplicate email event for RequestId: {}", payload.getRequestId());
            return;
        }

        try {
            EmailRequest emailRequest = new EmailRequest();
            emailRequest.setTo(payload.getRecipientEmail());
            emailRequest.setSubject(payload.getSubject());
            emailRequest.setMessage(payload.getBody());

            String messageId = awsSesEmailService.sendEmail(emailRequest);
            logger.info("Successfully processed email event for RequestId: {}. SES MessageId: {}",
                    payload.getRequestId(), messageId);
        } catch (RuntimeException e) {
            idempotencyStore.release(payload.getRequestId());
            throw e;
        }
    }

    private String maskEmail(String email) {
        int at = email.indexOf('@');
        return at <= 1 ? "***" : email.charAt(0) + "***" + email.substring(at);
    }
}
