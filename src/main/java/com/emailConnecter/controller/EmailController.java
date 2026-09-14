package com.emailConnecter.controller;

import com.emailConnecter.constants.Constant;
import com.emailConnecter.exception.InfisicalConfigurationException;
import com.emailConnecter.exception.InfisicalFetchException;
import com.emailConnecter.request.EmailRequest;
import com.emailConnecter.response.EmailResponse;
import com.emailConnecter.response.EmailResponseStatus;
import com.emailConnecter.service.AwsSesEmailService;
import com.emailConnecter.utils.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import software.amazon.awssdk.services.ses.model.SesException;
import com.emailConnecter.exception.SecretNotFoundException;
import jakarta.validation.Valid;

/**
 * Controller for handling email-related REST API requests.
 */
@RestController
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
    public ResponseEntity<EmailResponse> sendEmail(@Valid @RequestBody EmailRequest emailRequest) {
        logger.info("Received request to send email to: {}", Helper.maskString(emailRequest.getTo()));
        try {
            String messageId = awsSesEmailService.sendEmail(emailRequest);
            EmailResponse response = EmailResponse.builder()
                    .status(EmailResponseStatus.SUCCESS)
                    .statusCode(HttpStatus.OK.value())
                    .message(Constant.EMAIL_SENT_SUCCESS_MESSAGE)
                    .messageId(messageId)
                    .build();
            logger.info("Successfully sent email to: {} with message ID: {}", Helper.maskString(emailRequest.getTo()), messageId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return errorResponse(HttpStatus.valueOf(e.getStatusCode().value()), e.getReason());
        } catch (SesException e) {
            logger.error("Failed to send email to: {}. Error: {}", Helper.maskString(emailRequest.getTo()), e.getMessage(), e);
            HttpStatus status = e.statusCode() >= 400 && e.statusCode() < 500
                    ? HttpStatus.valueOf(e.statusCode())
                    : HttpStatus.BAD_GATEWAY;
            return errorResponse(status, "Email delivery was rejected by the provider.");
        } catch (SecretNotFoundException | InfisicalConfigurationException e) {
            logger.error("Email service configuration is incomplete", e);
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Email service configuration is incomplete.");
        } catch (InfisicalFetchException e) {
            logger.error("Email service secret provider is unavailable", e);
            return errorResponse(HttpStatus.SERVICE_UNAVAILABLE, "Email service is temporarily unavailable.");
        } catch (Exception e) {
            logger.error("Failed to send email to: {}. Error: {}", Helper.maskString(emailRequest.getTo()), e.getMessage(), e);
            return errorResponse(HttpStatus.BAD_GATEWAY, "Email delivery is temporarily unavailable.");
        }
    }

    private ResponseEntity<EmailResponse> errorResponse(HttpStatus status, String message) {
        EmailResponse response = EmailResponse.builder()
                .status(EmailResponseStatus.ERROR)
                .statusCode(status.value())
                .message(message)
                .build();
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<EmailResponse> handleValidationFailure() {
        return errorResponse(HttpStatus.BAD_REQUEST, "Invalid email request.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<EmailResponse> handleUnreadableRequest() {
        return errorResponse(HttpStatus.BAD_REQUEST, "Request body is malformed.");
    }
}
