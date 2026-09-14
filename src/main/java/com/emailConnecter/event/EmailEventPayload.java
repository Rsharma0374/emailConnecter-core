package com.emailConnecter.event;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Value
@Builder
@Jacksonized
public class EmailEventPayload {
    @NotNull
    private UUID requestId;
    @NotBlank
    @Email
    @Size(max = 254)
    private String recipientEmail;
    @NotBlank
    @Size(max = 998)
    private String subject;
    @NotBlank
    @Size(max = 100_000)
    private String body;
    @Email
    @Size(max = 254)
    private String fromAddress;
}
