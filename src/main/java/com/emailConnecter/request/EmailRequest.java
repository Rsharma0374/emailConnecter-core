package com.emailConnecter.request;

import lombok.Getter;

/**
 * Represents a request to send an email.
 * This class encapsulates the basic components needed for an email.
 */
@Getter
public class EmailRequest {

    /**
     * The recipient's email address.
     */
    private String to;

    /**
     * The subject line of the email.
     */
    private String subject;

    /**
     * The body/message content of the email.
     */
    private String message;

    /**
     * Sets the recipient's email address.
     *
     * @param to the email address to send to.
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * Sets the subject of the email.
     *
     * @param subject the subject line.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Sets the message body of the email.
     *
     * @param message the content of the email.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns a string representation of the EmailRequest object.
     *
     * @return a string describing the object's state.
     */
    @Override
    public String toString() {
        return "EmailRequest{" +
                "to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

}
