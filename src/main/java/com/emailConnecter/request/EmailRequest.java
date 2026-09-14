package com.emailConnecter.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents a request to send an email.
 * This class encapsulates the basic components needed for an email.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class EmailRequest {

    /**
     * The recipient's email address.
     */
    @NotBlank(message = "Recipient email is required")
    @Email(message = "Recipient email must be valid")
    @Size(max = 254, message = "Recipient email is too long")
    private String to;

    /**
     * The subject line of the email.
     */
    @NotBlank(message = "Subject is required")
    @Size(max = 998, message = "Subject is too long")
    private String subject;

    /**
     * The body/message content of the email.
     */
    @NotBlank(message = "Message is required")
    @Size(max = 100_000, message = "Message is too long")
    private String message;

    /**
     * Returns a string representation of the EmailRequest object.
     *
     * @return a string describing the object's state.
     */
    @Override
    public String toString() {
        return "EmailRequest{" +
                "to='" + maskEmail(to) + '\'' +
                ", subjectLength=" + (subject == null ? 0 : subject.length()) +
                ", messageLength=" + (message == null ? 0 : message.length()) +
                '}';
    }

    private String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return email;
        }
        int at = email.indexOf('@');
        return at <= 1 ? "***" : email.charAt(0) + "***" + email.substring(at);
    }
}
