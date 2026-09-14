package com.emailConnecter.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a message request coming from a portfolio contact form.
 */
@EqualsAndHashCode
@Getter
@Setter
public class PortfolioMessageRequest {

    /**
     * The name of the sender.
     */
    @NotBlank
    @Size(max = 200)
    @JsonProperty("Name")
    private String name;

    /**
     * The email address provided by the sender.
     */
    @NotBlank
    @Email
    @Size(max = 254)
    @JsonProperty("Email")
    private String email;

    /**
     * The message content sent by the user.
     */
    @NotBlank
    @Size(max = 100_000)
    @JsonProperty("Message")
    private String message;

    @Override
    public String toString() {
        return "PortfolioMessageRequest{" +
                "nameLength=" + (name == null ? 0 : name.length()) +
                ", emailPresent=" + (email != null && !email.isBlank()) +
                ", messageLength=" + (message == null ? 0 : message.length()) +
                '}';
    }

}