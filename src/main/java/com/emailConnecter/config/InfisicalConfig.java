package com.emailConnecter.config;

import com.emailConnecter.constants.Constant;
import com.emailConnecter.utils.Helper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infisical.sdk.InfisicalSdk;
import com.infisical.sdk.config.SdkConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Properties;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Configuration class for fetching secrets from Infisical.
 */
@Configuration
public class InfisicalConfig {

    private static final String INFISICAL_PATH = "/opt/configs/infisical.properties";

    private static final Logger logger = LoggerFactory.getLogger(InfisicalConfig.class);

    /**
     * Fetches configuration values from Infisical for a given secret name.
     *
     * @param secretName The name of the secret to retrieve.
     * @return A map containing the configuration values.
     */
    public Map<String, String> fetchConfig(String secretName) {
        if (secretName == null || secretName.isBlank()) {
            throw new IllegalArgumentException("Secret name must not be blank");
        }
        logger.info("Attempting to fetch config for secret: {}", secretName);
        Properties properties = Helper.fetchProperties(INFISICAL_PATH);

        try {
            if (properties == null) {
                throw new IllegalStateException("Unable to load Infisical properties from " + INFISICAL_PATH);
            }

            String infisicalUrl = requiredProperty(properties, "url");
            String infisicalToken = requiredProperty(properties, "token");
            String env = requiredProperty(properties, "env");
            logger.debug("Infisical URL: {}, Environment: {}", infisicalUrl, env);

            var sdk = new InfisicalSdk(new SdkConfig.Builder().withSiteUrl(infisicalUrl).build());
            sdk.Auth().SetAccessToken(infisicalToken);
            var secret = sdk.Secrets().GetSecret(
                    secretName, Constant.INFISICAL_PROJECT_ID, env, "/", null, null, null);

            if (secret == null || secret.getSecretValue() == null || secret.getSecretValue().isBlank()) {
                throw new IllegalStateException("Infisical returned no value for secret: " + secretName);
            }
            logger.debug("Successfully fetched secret: {}", secretName);
            return new ObjectMapper().readValue(
                    secret.getSecretValue(), new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            logger.error("Exception occurred while fetching config for secret: {}", secretName, e);
            throw new IllegalStateException("Failed to fetch secret from Infisical: " + secretName, e);
        }
    }

    private String requiredProperty(Properties properties, String name) {
        String value = properties.getProperty(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing Infisical property: " + name);
        }
        return value;
    }
}