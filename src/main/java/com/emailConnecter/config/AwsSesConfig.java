package com.emailConnecter.config;

import com.emailConnecter.constants.Constant;
import com.emailConnecter.service.InfisicalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

/**
 * Configuration class for setting up the AWS SES client.
 */
@Configuration
public class AwsSesConfig {

    private static final Logger logger = LoggerFactory.getLogger(AwsSesConfig.class);

    private final InfisicalService infisicalService;

    /**
     * Constructs AwsSesConfig with required dependencies.
     * 
     * @param infisicalService Service to fetch secrets.
     */
    @Autowired
    public AwsSesConfig(InfisicalService infisicalService) {
        this.infisicalService = infisicalService;
    }

    /**
     * Creates and configures the SesClient bean using credentials and region
     * fetched from InfisicalService.
     *
     * @return A configured SesClient instance.
     */
    @Bean
    public SesClient sesClient() {
        logger.info("Initializing SES Client");
        try {
            String accessKey = infisicalService.getSecret(Constant.AWS_ACCESS_KEY);
            String secretKey = infisicalService.getSecret(Constant.AWS_SECRET_KEY);
            String region = infisicalService.getSecret(Constant.AWS_REGION);

            AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
                    accessKey,
                    secretKey
            );

            logger.debug("Building SES Client for region: {}", region);
            return SesClient.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                    .build();
        } catch (Exception e) {
            logger.error("Failed to initialize SES Client", e);
            throw new RuntimeException("Error initializing SES client", e);
        }
    }
}