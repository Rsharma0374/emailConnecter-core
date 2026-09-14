package com.emailConnecter.service;

import com.emailConnecter.config.CacheConfig;
import com.emailConnecter.config.InfisicalConfig;
import com.emailConnecter.exception.SecretNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * Service class for interacting with Infisical to retrieve secrets.
 * Implements an in-memory caching mechanism to optimize repeated secret fetches.
 */
@Service
public class InfisicalService {

    private static final Logger logger = LoggerFactory.getLogger(InfisicalService.class);
    public static final String AMAZON_SES = "amazon_ses";
    private final CacheConfig cacheConfig;
    private final InfisicalConfig infisicalConfig;

    public InfisicalService(CacheConfig cacheConfig, InfisicalConfig infisicalConfig) {
        this.cacheConfig = cacheConfig;
        this.infisicalConfig = infisicalConfig;
    }

    /**
     * Retrieves a secret by its name. Checks the local cache first, 
     * and if not found, queries Infisical.
     *
     * @param secretName the name of the secret to retrieve.
     * @return the nonblank value of the requested secret.
     * @throws IllegalStateException if Infisical cannot provide the requested secret.
     */
    public String getSecret(String secretName) {
        if (secretName == null || secretName.isBlank()) {
            throw new IllegalArgumentException("Secret name must not be blank");
        }
        logger.debug("Requesting secret: {}", secretName);
        Optional<String> cachedSecret = cacheConfig.get(secretName);
        if (cachedSecret.isPresent()) {
            logger.debug("Secret '{}' found in cache", secretName);
            return cachedSecret.get();
        }

        logger.info("Secret '{}' not in cache. Fetching from Infisical...", secretName);
        Map<String, String> configMap = infisicalConfig.fetchConfig(AMAZON_SES);
        if (configMap.isEmpty()) {
            throw new IllegalStateException("Infisical returned no values for secret: " + secretName);
        }

        configMap.forEach((key, value) -> {
            if (key != null && !key.isBlank() && value != null && !value.isBlank()) {
                cacheConfig.put(key, value);
            }
        });

        return cacheConfig.get(secretName)
                .orElseThrow(() -> new SecretNotFoundException(
                        "Required secret '" + secretName + "' was not returned by Infisical"));
    }
}
