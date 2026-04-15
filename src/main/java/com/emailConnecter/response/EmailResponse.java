package com.emailConnecter.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the response sent back after attempting to send an email.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailResponse {
    
    /**
     * The status of the operation (e.g., "Success", "Error").
     */
    private String status;
    
    /**
     * The HTTP status code associated with the outcome.
     */
    private int statusCode;
    
    /**
     * A descriptive message regarding the outcome.
     */
    private String message;
    
    /**
     * The unique message ID assigned by the email service provider (if successful).
     */
    private String messageId;
}
