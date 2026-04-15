package com.emailConnecter.controller;

import com.emailConnecter.constants.Constant;
import com.emailConnecter.request.EmailRequest;
import com.emailConnecter.response.EmailResponse;
import com.emailConnecter.service.AwsSesEmailService;
import com.emailConnecter.utils.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling email-related REST API requests.
 */
@RestController
@RequestMapping("/email-connector")
public class EmailController {
    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

    private final AwsSesEmailService awsSesEmailService;

    /**
     * Constructs EmailController with the required AwsSesEmailService.
     *
     * @param awsSesEmailService the AWS SES email service to be used for sending emails.
     */
    @Autowired
    public EmailController(AwsSesEmailService awsSesEmailService) {
        this.awsSesEmailService = awsSesEmailService;
    }

    /**
     * A simple welcome endpoint.
     *
     * @return A welcome message string.
     */
    @GetMapping("/welcome")
    public String welcome() {
        logger.info("Welcome endpoint accessed.");
        return "   ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥\n" +
                "  ♥                                                 ♥\n" +
                " ♥                  Welcome to                       ♥\n" +
                " ♥               Email Connector                     ♥\n" +
                "  ♥                                                 ♥\n" +
                "   ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥\n";
    }

    /**
     * Endpoint to send an email using AWS SES.
     *
     * @param emailRequest The request body containing the email details.
     * @return A ResponseEntity containing an EmailResponse indicating success or failure.
     */
    @PostMapping("/send-mail")
    public ResponseEntity<EmailResponse> sendEmail(@RequestBody EmailRequest emailRequest) {
        logger.info("Received request to send email to: {}", Helper.maskString(emailRequest.getTo()));
        try {
            String messageId = awsSesEmailService.sendEmail(emailRequest);
            EmailResponse response = EmailResponse.builder()
                    .status(Constant.SUCCESS)
                    .statusCode(HttpStatus.OK.value())
                    .message(Constant.EMAIL_SENT_SUCCESS_MESSAGE)
                    .messageId(messageId)
                    .build();
            logger.info("Successfully sent email to: {} with message ID: {}", Helper.maskString(emailRequest.getTo()), messageId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to send email to: {}. Error: {}", Helper.maskString(emailRequest.getTo()), e.getMessage(), e);
            EmailResponse response = EmailResponse.builder()
                    .status(Constant.ERROR)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(Constant.EMAIL_SENT_FAILED_MESSAGE + e.getMessage())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
