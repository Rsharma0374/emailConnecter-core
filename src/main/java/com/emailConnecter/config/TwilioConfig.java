package com.emailConnecter.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class TwilioConfig {


    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.account.token}")
    private String authToken;

    @Value("${twilio.account.number}")
    private String trialNumber;
}
