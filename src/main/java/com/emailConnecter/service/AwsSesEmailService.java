package com.emailConnecter.service;

import com.emailConnecter.constants.Constant;
import com.emailConnecter.request.EmailRequest;
import com.emailConnecter.utils.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

/**
 * Service for sending emails using AWS Simple Email Service (SES).
 * This class encapsulates the logic for creating and sending an email,
 * including handling of secrets and error logging.
 */
@Service
public class AwsSesEmailService {
    private static final Logger logger = LoggerFactory.getLogger(AwsSesEmailService.class);

    private final SesClient sesClient;
    private final InfisicalService infisicalService;

    /**
     * Constructs an AwsSesEmailService with the given SesClient and InfisicalService.
     *
     * @param sesClient The AWS SES client.
     * @param infisicalService The service for retrieving secrets.
     */
    @Autowired
    public AwsSesEmailService(SesClient sesClient, InfisicalService infisicalService) {
        this.sesClient = sesClient;
        this.infisicalService = infisicalService;
    }

    /**
     * Sends an email using AWS SES.
     *
     * @param emailRequest The email request containing the recipient, subject, and message.
     * @return The message ID of the sent email.
     * @throws Exception if an error occurs while sending the email.
     */
    public String sendEmail(EmailRequest emailRequest) throws Exception {
        logger.info("Attempting to send email to: {}", Helper.maskString(emailRequest.getTo()));
        try {
            String fromEmail = infisicalService.getSecret(Constant.AWS_SES_FROM_EMAIL);
            Destination destination = Destination.builder()
                    .toAddresses(emailRequest.getTo())
                    .build();

            Content subjectContent = Content.builder()
                    .data(emailRequest.getSubject())
                    .build();

            Content bodyContent = Content.builder()
                    .data(emailRequest.getMessage())
                    .build();

            Body emailBody = Body.builder()
                    .text(bodyContent)
                    .build();

            Message message = Message.builder()
                    .subject(subjectContent)
                    .body(emailBody)
                    .build();

            SendEmailRequest request = SendEmailRequest.builder()
                    .source(fromEmail)
                    .destination(destination)
                    .message(message)
                    .build();

            logger.debug("Sending email with request: {}", request);
            SendEmailResponse sendEmailResponse = sesClient.sendEmail(request);
            logger.info("Email sent successfully to: {} with message ID: {}", Helper.maskString(emailRequest.getTo()), sendEmailResponse.messageId());
            return sendEmailResponse.messageId();
        } catch (Exception e) {
            logger.error("Failed to send email to: {}. Error: {}", Helper.maskString(emailRequest.getTo()), e.getMessage(), e);
            throw new Exception("Failed to send email: " + e.getMessage(), e);
        }
    }
}