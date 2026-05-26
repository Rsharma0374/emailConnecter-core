package com.emailConnecter.consumer;

import com.emailConnecter.event.EmailEventPayload;
import com.emailConnecter.request.EmailRequest;
import com.emailConnecter.service.AwsSesEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationConsumer.class);

    private final AwsSesEmailService awsSesEmailService;

    @Autowired
    public EmailNotificationConsumer(AwsSesEmailService awsSesEmailService) {
        this.awsSesEmailService = awsSesEmailService;
    }

    @KafkaListener(topics = "email-notifications", groupId = "${spring.kafka.consumer.group-id:email-connector-group}")
    public void consumeEmailNotification(EmailEventPayload payload) {
        logger.info("Received email notification event. RequestId: {}, To: {}", 
                payload.getRequestId(), payload.getRecipientEmail());

        try {
            EmailRequest emailRequest = new EmailRequest();
            emailRequest.setTo(payload.getRecipientEmail());
            emailRequest.setSubject(payload.getSubject());
            emailRequest.setMessage(payload.getBody());
            
            // Note: fromAddress in payload is not directly used here since AwsSesEmailService 
            // relies on Infisical to get the configured AWS_SES_FROM_EMAIL.

            String messageId = awsSesEmailService.sendEmail(emailRequest);
            logger.info("Successfully processed email event for RequestId: {}. SES MessageId: {}", 
                    payload.getRequestId(), messageId);
        } catch (Exception e) {
            logger.error("Failed to process email event for RequestId: {}. Error: {}", 
                    payload.getRequestId(), e.getMessage(), e);
            // Depending on the retry policy, you might want to rethrow the exception or handle it
        }
    }
}
