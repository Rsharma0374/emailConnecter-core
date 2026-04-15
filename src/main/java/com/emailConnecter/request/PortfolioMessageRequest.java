package com.emailConnecter.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a message request coming from a portfolio contact form.
 */
public class PortfolioMessageRequest {

    /**
     * The name of the sender.
     */
    @JsonProperty("Name")
    private String name;

    /**
     * The email address provided by the sender.
     */
    @JsonProperty("Email")
    private String queryEmailId;

    /**
     * The message content sent by the user.
     */
    @JsonProperty("Message")
    private String message;

    /**
     * Retrieves the sender's name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the sender's name.
     *
     * @param name the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the sender's email address.
     *
     * @return the sender's email.
     */
    public String getQueryEmailId() {
        return queryEmailId;
    }

    /**
     * Sets the sender's email address.
     *
     * @param queryEmailId the email to set.
     */
    public void setQueryEmailId(String queryEmailId) {
        this.queryEmailId = queryEmailId;
    }

    /**
     * Retrieves the message content.
     *
     * @return the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message content.
     *
     * @param message the message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

}